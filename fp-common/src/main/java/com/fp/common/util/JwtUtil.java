package com.fp.common.util;

import com.fp.common.constant.JwtClaimsConstant;
import com.fp.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Deprecated
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    /**
     * Generate a JWT token for users.
     * @param accountId Field for Account entity. the id of the user, used for claims.
     * @param email Field for Account entity. the email of the user, used for claims.
     * @param name Field for Account entity. the name of the user, used for claims.
     * @return
     */
    public String generateAccessToken(Long accountId, String email, String name){
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ACCOUNT_ID, accountId);
        claims.put(JwtClaimsConstant.NAME, name);
        claims.put(JwtClaimsConstant.TYPE, "access");
        return generateToken(
                claims,
                email,
                jwtProperties.getAccessToken().getExpirationInMillis()
        );
    }

    /**
     * generate a JWT refresh token for users.
     * @param accountId
     * @param email
     * @return
     */
    public String generateRefreshToken(Long accountId, String email){
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ACCOUNT_ID, accountId);
        claims.put(JwtClaimsConstant.TYPE, "refresh");
        jwtProperties.getRefreshToken().setExpiration(Duration.ofDays(7));
        return generateToken(
                claims,
                email,
                jwtProperties.getRefreshToken().getExpirationInMillis()
        );
    }

    /**
     * Generate a JWT token with the given claims and secret key.
     * @param claims
     * @param subject
     * @param expInMillis
     * @return
     */
    private String generateToken(Map<String, Object> claims, String subject, long expInMillis) {
        claims.put("iss", jwtProperties.getIssuer());
        claims.put("aud", jwtProperties.getAudience());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expInMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Use HMAC SHA-256 generating the signature with the formatted key
                .compact();
    }

    /**
     * Get the signing key for JWT.
     * This is not genearting the actual cryptographic key, but rather generating a key
     * correctly formatted for HAMC SHA-256 signing.
     * @return
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    private Duration getAccessTokenExpirationDuration(){
        return jwtProperties.getAccessToken().getExpiration();
    }
    private Duration getRefreshTokenExpirationDuration(){
        return jwtProperties.getRefreshToken().getExpiration();
    }

    public Claims validateRefreshToken(String refreshToken) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            Claims claims = validateRefreshToken(refreshToken);
            // Check if the token type is refresh

            // Check if expired

            // Check ... A lot.

            return claims.get(JwtClaimsConstant.TYPE).equals("refresh");
        } catch (JwtException e) {
           return false;
        }
    }

    public Long getAccountIdByRefreshToken(String refreshToken) {
        Claims claims = validateRefreshToken(refreshToken);
        return claims.get(JwtClaimsConstant.ACCOUNT_ID, Long.class);
    }
}
