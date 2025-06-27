package com.fp.service;

import com.fp.constant.JwtClaimsKey;
import com.fp.enumeration.jwt.JwtType;
import com.fp.exception.business.AccountNotFoundException;
import com.fp.exception.business.JwtRepositoryNotFoundException;
import com.fp.exception.business.JwtRevokedException;
import com.fp.exception.business.JwtRevokingException;
import com.fp.properties.JwtProperties;
import com.fp.repository.RevokedJwtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.*;


@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    @Autowired(required = false) // Not all services have DynamoDB dependency
    private RevokedJwtRepository revokedJwtRepository;

    /**
     * Generate access token using Spring Security JwtEncoder.
     *
     * @param accountId the account ID of the user
     * @param email the email of the user
     * @param name the name of the user
     * @return the generated access token
     */
    public String generateAccessToken(String accountId, String email, String name) {
        Map<String, Object> claims = Map.of(
                JwtClaimsKey.ACCOUNT_ID, accountId,
                JwtClaimsKey.NAME, name,
                JwtClaimsKey.TYPE, jwtProperties.getAccessTokenConfig().getType()
        );
        return generateToken(claims, email, jwtProperties.getAccessTokenConfig());
    }

    /**
     * Generate refresh token using Spring Security JwtEncoder
     *
     * @param accountId the account ID of the user
     * @param email the email of the user
     * @return the generated refresh token
     */
    public String generateRefreshToken(String accountId, String email) {
        Map<String, Object> claims = Map.of(
                JwtClaimsKey.ACCOUNT_ID, accountId,
                JwtClaimsKey.TYPE, jwtProperties.getRefreshTokenConfig().getType()
        );

        return generateToken(claims, email, jwtProperties.getRefreshTokenConfig());
    }

    public String generateVerifyToken(String accountId, String email){
        Map<String, Object> claims = Map.of(
                JwtClaimsKey.ACCOUNT_ID, accountId,
                JwtClaimsKey.TYPE, jwtProperties.getVerifyTokenConfig().getType()
        );
        return generateToken(claims, email, jwtProperties.getVerifyTokenConfig());
    }

    private String generateToken(Map<String, Object> claims, String email, JwtProperties.TokenConfig tokenConfig){
        var now = Instant.now();
        // Create JWS header with HS256 algorithm; Default is RS256
        var jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        var claimsSet = JwtClaimsSet.builder()
                .claims(map -> map.putAll(claims))
                .id(UUID.randomUUID().toString()) // jti
                .subject(email)
                .issuedAt(now)
                .issuer(tokenConfig.getIssuer())
                .audience(List.of(tokenConfig.getAudience()))
                .expiresAt(now.plus(tokenConfig.getExpiration()))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();

    }

    /**
     * Validate the JWT token using Spring Security JwtDecoder.
     * @param token the JWT token to validate
     * @return the decoded Jwt object if the token is valid
     */
    public Jwt decodeAndValidate(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            if (isRevokedJwt(jwt)) {
                throw new JwtRevokedException();
            }
            return jwt;
        } catch (JwtException e) {
            log.error("Failed to decode JWT: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Revoke the JWT token
     * @param token the JWT token to validate and potentially revoke
     * @return the decoded Jwt object if the token is valid and not revoked
     */
    public Jwt revokeToken(String token, String reason) {
        try {
            Jwt jwt = decodeAndValidate(token);
            revokeJwt(jwt, "Token type:" + jwt.getClaimAsString(JwtClaimsKey.TYPE) + "revoked for reason: " + reason);
            return jwt;
        } catch (JwtException e) {
            throw new JwtRevokingException(e);
        }
    }

    private boolean isRevokedJwt(Jwt jwt) {
        if (revokedJwtRepository == null) {
            log.warn("RevokedTokenRepository is null: The invoking service does not have DynamoDB service enabled.");
            throw new JwtRepositoryNotFoundException();
        }
        return revokedJwtRepository.exists(jwt);
    }

    public void revokeJwt(Jwt jwt, String reason) {
        if(revokedJwtRepository == null){
            log.warn("RevokedTokenRepository is null: The invoking service does not have DynamoDB service enabled.");
            throw new JwtRepositoryNotFoundException();
        }
        revokedJwtRepository.revokeJwt(jwt, reason);
    }

    public boolean isTokenType(String token, JwtType expectedType) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE)).equals(expectedType);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        return isTokenType(token, JwtType.REFRESH);
    }

    public boolean isAccessToken(String token) {
        return isTokenType(token, JwtType.ACCESS);
    }

    public Optional<String> getAccountIdFromAuthContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return Optional.of(jwt.getClaim(JwtClaimsKey.ACCOUNT_ID));
        }
        return Optional.empty();
    }
    public Optional<String> getAccountIdFromToken(String token) {
        try {
            Jwt jwt = decodeAndValidate(token);
            return Optional.of(jwt.getClaim(JwtClaimsKey.ACCOUNT_ID));
        } catch (JwtException e) {
            log.error("Failed to get account ID from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getEmailFromToken(String token) {
        try {
            Jwt jwt = decodeAndValidate(token);
            return Optional.of(jwt.getSubject());
        } catch (JwtException e) {
            log.error("Failed to get email from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getEmailFromAuthContext(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return Optional.of(jwt.getSubject());
        }
        return Optional.empty();
    }
    public Jwt getJwtFromAuthContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new AccountNotFoundException("JWT token not found in authentication context");
    }
}
