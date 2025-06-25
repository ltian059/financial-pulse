package com.fp.service.impl;

import com.fp.entity.Account;
import com.fp.repository.AccountRepository;
import com.fp.service.AccountService;
import com.fp.service.SesService;
import com.fp.constant.Messages;
import com.fp.enumeration.api.FollowServiceAPI;
import com.fp.dto.auth.LoginDTO;
import com.fp.exception.business.*;
import com.fp.exception.service.InvalidRefreshTokenException;
import com.fp.exception.ServiceException;
import com.fp.dto.auth.CreateAccountDTO;
import com.fp.service.JwtService;
import com.fp.vo.auth.LoginVO;
import com.fp.vo.auth.RefreshTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
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
    private final JwtService jwtService;
    private final SesService sesService;


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
        if(sesService != null){
            try {
                sesService.sendVerificationEmail(account);
            } catch (Exception e) {
                throw new EmailSendingException("Failed to send verification email for account: " + account.getEmail(), e);
            }
        }

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
    public Account deleteAccountByEmail() {
        String email = jwtService.getEmailFromAuthContext();

        Key key = Key.builder().partitionValue(email).build();
        Optional<Account> account = accountRepository.deleteByKey(key);
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Account not found for email: " + email);
        }
        jwtService.revokeJwt(jwtService.getJwtFromAuthContext(), "Account deleted: " + email);
        return account.get();
    }

    @Override
    public void verifyAccountEmail(String verifyToken) {
        //1. Validate the verify jwt token
        try {
            Jwt jwt = jwtService.validateToken(verifyToken);
            String email = jwt.getSubject();
            //2. Check if the account exists
            Optional<Account> accountOpt = accountRepository.findByKey(Key.builder().partitionValue(email).build());
            if (accountOpt.isEmpty()) {
                throw new AccountNotFoundException("Account not found for email: " + email);
            }
            //3. Update the account to set verified to true
            Account account = accountOpt.get();
            account.setVerified(true);
            account.setModifiedAt(Instant.now());
            //4. Update the account in the DynamoDB
            accountRepository.updateItem(account, IgnoreNullsMode.SCALAR_ONLY);
        } catch (JwtException e) {
            throw new InvalidVerifyTokenException(Messages.Error.Auth.INVALID_TOKEN, e);
        }
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
        String accessToken = jwtService.generateAccessToken(id, email, name);
        String refreshToken = jwtService.generateRefreshToken(id, email);
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
     * @param refreshToken the refresh token to validate
     * @return RefreshTokenVO containing new access token and refresh token
     */
    @Override
    public RefreshTokenVO validateRefreshToken(String refreshToken) {
        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        //Use jwtUtil to parse the refresh token and extract user information
        Optional<String> idOpt = jwtService.getAccountIdFromToken(refreshToken);
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
                .accessToken(jwtService.generateAccessToken(id, account.getEmail(), account.getName()))
                .refreshToken(jwtService.generateRefreshToken(id, account.getEmail()))
                .build();
    }

}
