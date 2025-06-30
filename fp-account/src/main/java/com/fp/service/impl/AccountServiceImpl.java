package com.fp.service.impl;

import com.fp.client.FollowServiceClient;
import com.fp.dto.account.request.AccountVerifyRequestDTO;
import com.fp.dto.account.request.DeleteAccountRequestDTO;
import com.fp.dto.account.response.AccountResponseDTO;
import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.account.request.UpdateBirthdayRequestDTO;
import com.fp.entity.Account;
import com.fp.repository.AccountRepository;
import com.fp.repository.RevokedJwtRepository;
import com.fp.service.AccountService;
import com.fp.auth.service.JwtService;
import com.fp.service.SesService;
import com.fp.constant.Messages;
import com.fp.exception.business.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final FollowServiceClient followServiceClient;
    private final JwtService jwtService;
    private final SesService sesService;
    private final RevokedJwtRepository revokedJwtRepository;

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
        //1. Get the account info from the JWT context
        String email = jwtService.getEmailFromAuthContext()
                .orElseThrow(() -> new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR));
        //2. Revoke access token
        Jwt jwt = jwtService.getJwtFromAuthContext();
        revokedJwtRepository.revokeJwt(jwt, "User logged out");
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
    public Account deleteAccountByEmail(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        Account account = accountRepository.deleteAccount(deleteAccountRequestDTO.getAccountId(), deleteAccountRequestDTO.getEmail());

        revokedJwtRepository.revokeJwt(jwtService.getJwtFromAuthContext(), "Account deleted: " + account.getEmail());
        return account;
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
        followServiceClient.follow(followRequestDTO);
    }



}
