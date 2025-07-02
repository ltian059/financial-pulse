package com.fp.service.impl;

import com.fp.client.FollowServiceClient;
import com.fp.dto.account.request.AccountVerifyRequestDTO;
import com.fp.dto.account.request.DeleteAccountRequestDTO;
import com.fp.dto.account.response.AccountResponseDTO;
import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.account.request.UpdateBirthdayRequestDTO;
import com.fp.dto.follow.request.UnfollowRequestDTO;
import com.fp.entity.Account;
import com.fp.repository.AccountRepository;
import com.fp.service.AccountService;
import com.fp.service.SesService;
import com.fp.constant.Messages;
import com.fp.exception.business.*;
import com.fp.sqs.impl.MessageFactory;
import com.fp.sqs.service.EmailSqsService;
import com.fp.util.ServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final FollowServiceClient followServiceClient;
    private final SesService sesService;
    private final EmailSqsService sqsService;
    private final ThreadPoolTaskExecutor taskExecutor;

    /// Only for testing purposes, not used in production
    public void updateVerificationStatus(String email, boolean status){
        Account account = accountRepository.findByEmail(email);
        account.setVerified(status);
        account.setModifiedAt(Instant.now());
        accountRepository.updateItem(account, IgnoreNullsMode.SCALAR_ONLY);
    }

    @Override
    public AccountResponseDTO getAccountByEmail(String email) {
        Account byEmail = accountRepository.findByEmail(email);
        AccountResponseDTO dto = new AccountResponseDTO();
        BeanUtils.copyProperties(byEmail, dto);
        return dto;
    }

    @Override
    public void logout() {
    }

    @Override
    public void updateBirthday(UpdateBirthdayRequestDTO birthdayRequestDTO) {
        try {
            LocalDate parsedBirthday = DateUtils.parseDateStrictly(birthdayRequestDTO.getBirthday(), "MM/dd/yyyy")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            // Validate the birthday is not in the future
            if(parsedBirthday.isAfter(LocalDate.now())){
                throw new BirthdayInFutureException();
            }
            // Update the account's birthday
            accountRepository.updateItem(
                    Account.builder().email(birthdayRequestDTO.getEmail()).birthday(parsedBirthday).build(),
                    IgnoreNullsMode.SCALAR_ONLY
            );
        } catch (ParseException e) {
            throw new BirthdayFormatParseException(e);
        }
    }

    @Override
    public void unfollow(UnfollowRequestDTO unfollowRequestDTO) {
        followServiceClient.unfollow(unfollowRequestDTO);
    }

    @Override
    public Account deleteAccountByEmail(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        return accountRepository.deleteAccount(deleteAccountRequestDTO.getAccountId(), deleteAccountRequestDTO.getEmail());
    }



    @Override
    public void sendVerificationEmail(AccountVerifyRequestDTO verifyRequestDTO) {
        //Query the account from DynamoDB, make sure the account info is valid
        Account account = accountRepository.findByEmail(verifyRequestDTO.getEmail());
        if(account.getVerified()){
            throw new AccountAlreadyVerifiedException(Messages.Error.Account.ALREADY_VERIFIED + verifyRequestDTO.getEmail());
        }
        sesService.sendVerificationEmail(account);
    }

    @Override
    public Long getFollowerCountById(String accountId) {
        return followServiceClient.getFollowerCountById(accountId);
    }

    @Override
    public void follow(FollowRequestDTO followRequestDTO) {
        //1. Send follow request to follow service
        try {
            followServiceClient.follow(followRequestDTO);
            //2. if the followee is open to receive follower notifications, find the followee account and send a notification asynchronously
            CompletableFuture
                    .runAsync(() -> sendFollowerNotificationMessage(followRequestDTO.getEmail(), followRequestDTO.getFolloweeId()), taskExecutor)
                    .exceptionally(throwable -> {
                log.error("Failed to send follower notification message async", throwable);
                return null;
            });
        } catch (WebClientResponseException e) {
            throw ServiceExceptionHandler.handleFollowServiceWebClientException(e);
        }


    }

    /**
     * General second index searching in dynamodb is slower, so we use async to avoid blocking the main thread.
     */
    protected void sendFollowerNotificationMessage(String followerEmail, String followeeId) {
        Account follower = accountRepository.findByEmail(followerEmail);
        Optional<Account> followee = accountRepository.
                findByAccountId(followeeId);
        if (followee.isEmpty()) {
            log.warn("Followee not found for notification: {}", followeeId);
            throw new IllegalArgumentException("Followee not found for notification: " + followeeId);
        }
        //Check followee account setting, if the followee is willing to receive follower notifications, publish a notification message to SQS
        //TODO add account setting

        var followerNotificationMessage = MessageFactory.createFollowerNotificationMessage(
                follower.getName(),
                followeeId,
                followee.get().getName(),
                followee.get().getEmail(),
                "account-service: sendFollowerNotificationMessage"
        );
        //Push the message to SQS
        try {
            sqsService.sendEmailMessage(followerNotificationMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed sending follower notification", e);
        }
    }


}
