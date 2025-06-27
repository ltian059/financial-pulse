package com.fp.repository;

import com.fp.constant.JwtClaimsKey;
import com.fp.entity.RevokedJwt;
import com.fp.enumeration.jwt.JwtType;
import org.springframework.security.oauth2.jwt.Jwt;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Instant;


public class RevokedJwtRepository extends DynamoDbRepository<RevokedJwt>{
    /**
     * Revoke token
     */
    public void revokeJwt(Jwt jwt, String reason) {
        RevokedJwt revokedJwt = RevokedJwt.builder()
                .jti(jwt.getId())
                .ttl(jwt.getExpiresAt() != null ? jwt.getExpiresAt().getEpochSecond() : 0)
                .reason(reason)
                .revokedAt(Instant.now())
                .type(JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE)))
                .accountId(jwt.getClaimAsString(JwtClaimsKey.ACCOUNT_ID))
                .build();
        save(revokedJwt);
    }

    public boolean exists(Jwt jwt) {
        return exists(Key.builder().partitionValue(jwt.getId()).build());
    }
}
