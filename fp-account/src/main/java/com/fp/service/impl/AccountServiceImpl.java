package com.fp.service.impl;

import com.fp.client.FollowServiceClient;
import com.fp.dto.account.AccountVerifyRequestDTO;
import com.fp.dto.account.DeleteAccountRequestDTO;
import com.fp.dto.account.FollowAccountRequestDTO;
import com.fp.entity.Account;
import com.fp.repository.AccountRepository;
import com.fp.service.AccountService;
import com.fp.service.SesService;
import com.fp.constant.Messages;
import com.fp.enumeration.api.FollowServiceAPI;
import com.fp.exception.business.*;
import com.fp.service.JwtService;
import com.fp.util.ServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final FollowServiceClient followServiceClient;
    private final JwtService jwtService;
    private final SesService sesService;

    /// Only for testing purposes, not used in production
    public void setVerificationStatus(String email, boolean status){
        Account account = accountRepository.findByEmail(email);
        account.setVerified(status);
        account.setModifiedAt(Instant.now());
        accountRepository.updateItem(account, IgnoreNullsMode.SCALAR_ONLY);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account deleteAccountByEmail(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        Account account = accountRepository.deleteAccount(deleteAccountRequestDTO.getAccountId(), deleteAccountRequestDTO.getEmail());

        jwtService.revokeJwt(jwtService.getJwtFromAuthContext(), "Account deleted: " + account.getEmail());
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
    public void followAccount(FollowAccountRequestDTO followAccountRequestDTO) {
        followServiceClient.followAccount(followAccountRequestDTO);
    }



}
