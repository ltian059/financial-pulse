package com.fp.account.service.impl;

import com.fp.account.entity.Account;
import com.fp.account.repository.AccountRepository;
import com.fp.account.service.AccountService;
import com.fp.common.api.FollowServiceAPI;
import com.fp.common.dto.auth.LoginDTO;
import com.fp.common.exception.business.AccountAlreadyExistsException;
import com.fp.common.exception.business.AccountNotFoundException;
import com.fp.common.exception.business.InvalidPasswordException;
import com.fp.common.exception.service.InvalidRefreshTokenException;
import com.fp.common.exception.ServiceException;
import com.fp.common.dto.auth.CreateAccountDTO;
import com.fp.common.service.JwtTokenService;
import com.fp.common.vo.auth.LoginVO;
import com.fp.common.vo.auth.RefreshTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient followWebClient;
    private final JwtTokenService jwtTokenService;


    @Override
    public void createAccount(CreateAccountDTO accountVO) {
        //Check if the email already exists.
        Key key = Key.builder().partitionValue(accountVO.getEmail()).build();
        if(accountRepository.exists(key)){
            throw new AccountAlreadyExistsException("Email already exists: " + accountVO.getEmail());
        }
        // Convert AccountDTO to Account entity
        String encryptedPassword = passwordEncoder.encode(accountVO.getPassword());
        String accountId = UUID.randomUUID().toString();

        Account account = Account.builder()
                .accountId(accountId)
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
    public Account getAccountByEmail(String email) {
        Key key = Key.builder().partitionValue(email).build();
        Optional<Account> byKey = accountRepository.findByKey(key);
        if (byKey.isEmpty()){
            throw new AccountNotFoundException("Account not found for email: " + email);
        } else {
            return byKey.get();
        }
    }

    @Override
    public void updateVerificationStatus(boolean verified) {
        String email = jwtTokenService.getEmailFromAuthContext();

        Account account = Account.builder().email(email).verified(verified).build();
        accountRepository.updateItem(account, IgnoreNullsMode.SCALAR_ONLY);
    }

    @Override
    public Account deleteAccountByEmail() {
        String email = jwtTokenService.getEmailFromAuthContext();

        Key key = Key.builder().partitionValue(email).build();
        Optional<Account> account = accountRepository.deleteByKey(key);
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Account not found for email: " + email);
        }
        //TODO Revoke JWT token if necessary
        return account.get();
    }

    @Override
    public Account getAccountById(String accountId) {
        Key key = Key.builder().partitionValue(accountId).build();
        Optional<Account> byKey = accountRepository.findByKey(key);
        if(byKey.isEmpty()) {
            throw new AccountNotFoundException("Account not found for ID: " + accountId);
        }
        return byKey.get();
    }

    @Override
    public Long getFollowerCountById(String accountId) {
        //TODO implement logic to get follower count
        return followWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(FollowServiceAPI.GET_FOLLOWER_COUNT_BY_ACCOUNT_ID.getPath())
                        .queryParam("accountId", accountId)
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

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        Optional<Account> byEmail = accountRepository.findByKey(Key.builder().partitionValue(loginDTO.getEmail()).build());
        if(byEmail.isEmpty()){
            throw new AccountNotFoundException();
        }
        String encryptedPassword = byEmail.get().getEncryptedPassword();
        if(!passwordEncoder.matches(password, encryptedPassword)){
            throw new InvalidPasswordException();
        }
        var account = byEmail.get();
        var id = account.getAccountId();
        var name = account.getName();
        //After login successfully, generate JWT token
        String accessToken = jwtTokenService.generateAccessToken(id, email, name);
        String refreshToken = jwtTokenService.generateRefreshToken(id, email);
        var loginVO = new LoginVO();
        BeanUtils.copyProperties(account, loginVO);
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        log.info("Account login successful: {}", loginVO);
        return loginVO;
    }

    /**
     * Validate refresh token, if valid:
     * return the refreshTokenVO with new access token and optionally a new refresh token.
     *
     * @param refreshToken
     * @return
     */
    @Override
    public RefreshTokenVO validateRefreshToken(String refreshToken) {
        if(!jwtTokenService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        //Use jwtUtil to parse the refresh token and extract user information
        Optional<String> idOpt = jwtTokenService.getAccountIdFromToken(refreshToken);
        if(idOpt.isEmpty()){
            throw new InvalidRefreshTokenException();
        }
        String id = idOpt.get();
        Optional<Account> accountOpt = accountRepository.findByKey(Key.builder().partitionValue(id).build());
        if(accountOpt.isEmpty()){
            throw new AccountNotFoundException();
        }
        Account account = accountOpt.get();
        //Generate new access token
        return RefreshTokenVO.builder()
                .accessToken(jwtTokenService.generateAccessToken(id, account.getEmail(), account.getName()))
                .refreshToken(jwtTokenService.generateRefreshToken(id, account.getEmail()))
                .build();
    }

}
