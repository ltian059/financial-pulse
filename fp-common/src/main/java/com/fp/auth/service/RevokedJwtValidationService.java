package com.fp.auth.service;


import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Service interface for validating whether JWT tokens have been revoked.
 *
 * This interface abstracts the revoked token validation logic, allowing
 * different implementations (local DynamoDB, remote service calls, etc.)
 */
public interface RevokedJwtValidationService {

    /**
     * Check if a JWT token has been revoked.
     *
     * @param jwt the JWT token to check
     * @return true if the token is revoked, false otherwise
     */
    boolean isTokenRevoked(Jwt jwt);

    /**
     * Check if a token with the given JTI has been revoked.
     *
     * @param jti the JWT ID to check
     * @return true if the token is revoked, false otherwise
     */
    boolean isTokenRevoked(String jti);

    /**
     * Revoke a JWT token.
     *
     * @param jwt the JWT token to revoke
     * @param reason the reason for revocation
     */
    void revokeJwt(Jwt jwt, String reason);

    void revokeToken(String token, String reason);

}
