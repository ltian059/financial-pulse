package com.fp.auth.service;

import com.fp.enumeration.jwt.JwtType;
import org.springframework.security.oauth2.jwt.*;

import java.util.Optional;

public interface JwtService {
    /**
     * Generate access token using Spring Security JwtEncoder.
     *
     * @param accountId the account ID of the user
     * @param email the email of the user
     * @param name the name of the user
     * @return the generated access token
     */
    public String generateAccessToken(String accountId, String email, String name);

    /**
     * Generate refresh token using Spring Security JwtEncoder
     *
     * @param accountId the account ID of the user
     * @param email the email of the user
     * @return the generated refresh token
     */
    public String generateRefreshToken(String accountId, String email);

    public String generateVerifyToken(String accountId, String email);

    /**
     * Validate the JWT token using Spring Security JwtDecoder.
     * @param token the JWT token to validate
     * @return the decoded Jwt object if the token is valid
     */
    public Jwt decode(String token);


    public boolean isTokenType(String token, JwtType expectedType);

    public boolean isRefreshToken(String token);

    public boolean isAccessToken(String token);

    public Optional<String> getAccountIdFromAuthContext();

    public Optional<String> getAccountIdFromToken(String token);

    public Optional<String> getEmailFromToken(String token);

    public Optional<String> getEmailFromAuthContext();

    public Jwt getJwtFromAuthContext();
}
