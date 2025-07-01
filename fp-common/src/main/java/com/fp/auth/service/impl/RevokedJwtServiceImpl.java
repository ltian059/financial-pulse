package com.fp.auth.service.impl;

import com.fp.auth.service.JwtService;
import com.fp.auth.service.RevokedJwtService;
import com.fp.dynamodb.repository.RevokedJwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Service
@RequiredArgsConstructor
public class RevokedJwtServiceImpl implements RevokedJwtService {
    private final RevokedJwtRepository revokedJwtRepository;
    private final JwtService jwtService;

    @Override
    public boolean isTokenRevoked(Jwt jwt) {
        return revokedJwtRepository.exists(jwt);
    }

    @Override
    public boolean isTokenRevoked(String jti) {
        return revokedJwtRepository.exists(Key.builder().partitionValue(jti).build());
    }

    @Override
    public void revokeToken(String token, String reason) {
        Jwt jwt = null;
        try {
            jwt = jwtService.decode(token);
            revokeJwt(jwt, reason);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Error trying to revoke an invalid JWT token", e);
        }

    }

    @Override
    public void revokeJwt(Jwt jwt, String reason) {
        revokedJwtRepository.revokeJwt(jwt, reason);
    }
}
