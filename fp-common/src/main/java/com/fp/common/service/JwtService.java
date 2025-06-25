package com.fp.common.service;

import com.fp.common.constant.JwtClaimsKey;
import com.fp.common.exception.business.AccountNotFoundException;
import com.fp.common.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Generate access token using Spring Security JwtEncoder.
     *
     * @param accountId
     * @param email
     * @param name
     * @return
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
     * @param accountId
     * @param email
     * @return
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
                .subject(email)
                .issuedAt(now)
                .issuer(tokenConfig.getIssuer())
                .audience(List.of(tokenConfig.getAudience()))
                .expiresAt(now.plus(tokenConfig.getExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();

    }

    public Jwt validateToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            log.error("Failed to decode JWT: {}", e.getMessage());
            throw e;
        }
    }
    public boolean isTokenType(String token, String expectedType) {
        try {
            Jwt jwt = validateToken(token);
            return jwt.getClaimAsString(JwtClaimsKey.TYPE).equals(expectedType);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        return isTokenType(token, "refresh");
    }

    public boolean isAccessToken(String token) {
        return isTokenType(token, "access");
    }

    public Optional<String> getAccountIdFromToken(String token) {
        try {
            Jwt jwt = validateToken(token);
            return Optional.of(jwt.getClaim(JwtClaimsKey.ACCOUNT_ID));
        } catch (JwtException e) {
            log.error("Failed to get account ID from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }


    public String getEmailFromAuthContext(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getSubject();
        }
        throw new AccountNotFoundException("Email not found in authentication context");
    }
}
