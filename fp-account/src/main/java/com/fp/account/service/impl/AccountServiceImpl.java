package com.fp.account.service.impl;

import com.fp.account.entity.Account;
import com.fp.account.repository.AccountRepository;
import com.fp.account.service.AccountService;
import com.fp.common.api.FollowServiceAPI;
import com.fp.common.exception.ServiceException;
import com.fp.common.vo.account.CreateAccountVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient followWebClient;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder, WebClient followWebClient) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.followWebClient = followWebClient;
    }

    @Override
    public void createAccount(CreateAccountVO accountVO) {
        // Convert AccountDTO to Account entity
        String encryptedPassword = passwordEncoder.encode(accountVO.getPassword());
        Account account = Account.builder()
                .name(accountVO.getName())
                .email(accountVO.getEmail())
                .encryptedPassword(encryptedPassword)
                .verified(false)
                .createdAt(Instant.now())
                .build();
        //set default values
        accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void updateVerificationStatus(Long id, boolean verified) {
        Optional<Account> byId = accountRepository.findAccountById(id);
        if(byId.isPresent()){
            byId.get().setVerified(verified);
        }
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findAccountById(id);
    }

    @Override
    public Long getFollowerCountById(Long id) {
        //TODO implement logic to get follower count
        return followWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(FollowServiceAPI.GET_FOLLOWER_COUNT_BY_ACCOUNT_ID.getPath())
                        .queryParam("accountId", id)
                        .build()
                )
                .retrieve()
                .bodyToMono(Long.class)
                .block(); // Blocking call to get the follower count

    }

    @Override
    public void followAccount(Long accountId, Long followeeId) {
        try {
            followWebClient.method(FollowServiceAPI.FOLLOW_ACCOUNT.getMethod())
                    .uri(uriBuilder -> uriBuilder
                            .path(FollowServiceAPI.FOLLOW_ACCOUNT.getPath())
                            .queryParam("followerId", accountId)
                            .queryParam("followeeId", followeeId)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ServiceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }

}
