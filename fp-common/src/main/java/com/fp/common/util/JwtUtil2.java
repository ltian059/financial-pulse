package com.fp.common.util;

import com.fp.common.constant.JwtClaimsConstant;
import com.fp.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@RequiredArgsConstructor
@Slf4j
public class JwtUtil2 {

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
    public String generateAccessToken(Long accountId, String email, String name) {
        var now = Instant.now();

        var claims = Map.of(
                JwtClaimsConstant.ACCOUNT_ID, accountId,
                JwtClaimsConstant.NAME, name,
                JwtClaimsConstant.TYPE, "access"
        );
        var claimsSet = JwtClaimsSet.builder()
                .claims(map -> map.putAll(claims))
                .subject(email)
                .issuedAt(now)
                .audience(List.of(jwtProperties.getAudience()))
                .expiresAt(now.plus(jwtProperties.getAccessToken().getExpiration()))
                .build();
        // Create JWS header with HS256 algorithm; Default is RS256
        var jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    /**
     * Generate refresh token using Spring Security JwtEncoder
     *
     * @param accountId
     * @param email
     * @return
     */
    public String generateRefreshToken(Long accountId, String email) {
        var now = Instant.now();

        var claims = Map.of(
                JwtClaimsConstant.ACCOUNT_ID, accountId,
                JwtClaimsConstant.TYPE, "refresh"
        );
        var claimsSet = JwtClaimsSet.builder()
                .claims(map -> map.putAll(claims))
                .subject(email)
                .issuedAt(now)
                .audience(List.of(jwtProperties.getAudience()))
                .expiresAt(now.plus(jwtProperties.getRefreshToken().getExpiration()))
                .build();
        // Create JWS header with HS256 algorithm; Default is RS256
        var jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
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
            return jwt.getClaimAsString(JwtClaimsConstant.TYPE).equals(expectedType);
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

    public Optional<Long> getAccountIdFromToken(String token) {
        try {
            Jwt jwt = validateToken(token);
            return Optional.of(jwt.getClaim(JwtClaimsConstant.ACCOUNT_ID));
        } catch (JwtException e) {
            log.error("Failed to get account ID from JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }



}
