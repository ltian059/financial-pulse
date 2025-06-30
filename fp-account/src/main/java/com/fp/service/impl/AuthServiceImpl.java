package com.fp.service.impl;

import com.fp.auth.service.RevokedJwtValidationService;
import com.fp.constant.Messages;
import com.fp.dto.auth.request.CreateAccountRequestDTO;
import com.fp.dto.auth.request.LoginRequestDTO;
import com.fp.entity.Account;
import com.fp.exception.business.*;
import com.fp.exception.service.InvalidRefreshTokenException;
import com.fp.repository.AccountRepository;
import com.fp.repository.RevokedJwtRepository;
import com.fp.service.AuthService;
import com.fp.auth.service.JwtService;
import com.fp.service.SesService;
import com.fp.dto.auth.response.LoginResponseDTO;
import com.fp.dto.auth.response.RefreshTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SesService sesService;
    private final RevokedJwtValidationService revokedJwtValidationService;


    @Override
    public void createAccount(CreateAccountRequestDTO accountVO) {
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
        // Send verification email asynchronously
        if(sesService != null){
            try {
                sesService.sendVerificationEmail(account);
            } catch (Exception e) {
                throw new EmailSendingException("Failed to send verification email for account: " + account.getEmail(), e);
            }
        }
    }


    @Override
    //TODO Use AOP to revoke token
    public String verifyAccountEmail(String verifyToken) {
        //1. Validate the verify jwt token
        try {
            Jwt jwt = jwtService.decode(verifyToken);
            String email = jwt.getSubject();
            //2. Check if the account exists
            Account account = accountRepository.findByEmail(email);
            //3. Update the account to set verified to true
            if (account.getVerified()) {
                throw new AccountAlreadyVerifiedException(Messages.Error.Account.ALREADY_VERIFIED + email);
            }
            account.setVerified(true);
            account.setModifiedAt(Instant.now());
            //4. Update the account in the DynamoDB
            accountRepository.updateItem(account, IgnoreNullsMode.SCALAR_ONLY)
                    .orElseThrow(() -> new AccountNotFoundException(Messages.Error.Account.NOT_FOUND + email));

            revokedJwtValidationService.revokeJwt(jwt, "Token has been revoked after successful verification");

            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verified - Financial Pulse</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        margin: 0;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    }
                    .container {
                        background: white;
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
                        text-align: center;
                        max-width: 500px;
                    }
                    .success-icon {
                        font-size: 60px;
                        color: #28a745;
                        margin-bottom: 20px;
                    }
                    h1 {
                        color: #333;
                        margin-bottom: 20px;
                    }
                    p {
                        color: #666;
                        margin-bottom: 30px;
                        line-height: 1.6;
                    }
                    .btn {
                        display: inline-block;
                        padding: 12px 30px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 25px;
                        font-weight: bold;
                    }
                    .btn:hover {
                        opacity: 0.9;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="success-icon">✅</div>
                    <h1>Email Verified Successfully!</h1>
                    <p>
                        Congratulations! Your email address has been verified. 
                        Your Financial Pulse account is now fully activated and ready to use.
                    </p>
                    <a href="http://localhost:3000/login" class="btn">Continue to Login</a>
                </div>
            </body>
            </html>
            """;
        } catch (JwtException e) {
            throw new InvalidVerifyTokenException(Messages.Error.Auth.INVALID_TOKEN, e);
        }
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();
        Optional<Account> byEmail = accountRepository.findByKey(Key.builder().partitionValue(loginRequestDTO.getEmail()).build());
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
        var loginVO = new LoginResponseDTO();
        BeanUtils.copyProperties(account, loginVO);
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        log.debug("Account login successful: {}", loginVO);
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
    public RefreshTokenResponseDTO validateRefreshToken(String refreshToken) {
        try {
            if(!jwtService.isRefreshToken(refreshToken)) {
                throw new InvalidJwtTypeException(Messages.Error.Auth.INVALID_TOKEN_TYPE);
            }
            //Revoke the old refresh token
            //Use jwtUtil to parse the refresh token and extract user information
            Optional<String> emailOpt = jwtService.getEmailFromToken(refreshToken);
            if(emailOpt.isEmpty()){
                throw new InvalidRefreshTokenException();
            }
            String email = emailOpt.get();
            Optional<Account> accountOpt = accountRepository.findByKey(Key.builder().partitionValue(email).build());
            if(accountOpt.isEmpty()){
                throw new AccountNotFoundException();
            }
            Account account = accountOpt.get();
            //Generate new access token
            return RefreshTokenResponseDTO.builder()
                    .accessToken(jwtService.generateAccessToken(account.getAccountId(), account.getEmail(), account.getName()))
                    .refreshToken(jwtService.generateRefreshToken(account.getAccountId(), account.getEmail()))
                    .build();
        } catch (InvalidJwtTypeException | InvalidRefreshTokenException | AccountNotFoundException e ) {
            throw e;
        } finally {
            revokedJwtValidationService.revokeToken(refreshToken, "Refresh token has been used.");
        }
    }
}
