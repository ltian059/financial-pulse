package com.fp.account.service.impl;

import com.fp.account.entity.Account;
import com.fp.account.repository.AccountRepository;
import com.fp.account.service.AccountService;
import com.fp.common.api.FollowServiceAPI;
import com.fp.common.dto.auth.LoginDTO;
import com.fp.common.exception.business.AccountNotFoundException;
import com.fp.common.exception.business.InvalidPasswordException;
import com.fp.common.exception.service.InvalidRefreshTokenException;
import com.fp.common.exception.ServiceException;
import com.fp.common.dto.auth.CreateAccountDTO;
import com.fp.common.util.JwtUtil2;
import com.fp.common.vo.auth.LoginVO;
import com.fp.common.vo.auth.RefreshTokenVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient followWebClient;
    private final JwtUtil2 jwtUtil;


    @Override
    public void createAccount(CreateAccountDTO accountVO) {
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

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        Optional<Account> byEmail = accountRepository.findByEmail(email);
        if(byEmail.isEmpty()){
            throw new AccountNotFoundException();
        }
        String encryptedPassword = byEmail.get().getEncryptedPassword();
        if(!passwordEncoder.matches(password, encryptedPassword)){
            throw new InvalidPasswordException();
        }
        var account = byEmail.get();
        var id = account.getId();
        var name = account.getName();
        //After login successfully, generate JWT token
        String accessToken = jwtUtil.generateAccessToken(id, email, name);
        String refreshToken = jwtUtil.generateRefreshToken(id, email);
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
        if(!jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        //Use jwtUtil to parse the refresh token and extract user information
        Optional<Long> idOpt = jwtUtil.getAccountIdFromToken(refreshToken);
        if(idOpt.isEmpty()){
            throw new InvalidRefreshTokenException();
        }
        Long id = idOpt.get();
        Optional<Account> accountOpt = accountRepository.findAccountById(id);
        if(accountOpt.isEmpty()){
            throw new AccountNotFoundException();
        }
        Account account = accountOpt.get();
        //Generate new access token
        return RefreshTokenVO.builder()
                .accessToken(jwtUtil.generateAccessToken(id, account.getEmail(), account.getName()))
                .refreshToken(jwtUtil.generateRefreshToken(id, account.getEmail()))
                .build();
    }

}
