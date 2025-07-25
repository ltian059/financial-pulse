package com.fp.entity;

import com.fp.enumeration.jwt.JwtType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevokedJwt {

    /**
     * JWT "jti" (JWT ID) claim, which is a unique identifier for the token.
     */
    private String jti;

    private JwtType type;

    /**
     * The time when the token was revoked.
     */
    private Instant revokedAt;
    /**
     * The original expiration time of the token.
     */
    private Long ttl;

    private String reason;

    /**
     * The account ID associated with the revoked token.
     */
    private String accountId;

    @DynamoDbPartitionKey
    public String getJti() {
        return jti;
    }
    /**
     * TTL (Time To Live) attribute for the token, indicating when it should be considered expired.
     * DynamoDB will automatically delete the item after this time.
     * @return
     */
    @DynamoDbAttribute("ttl")
    public Long getTtl() {
        return ttl;
    }

    @DynamoDbAttribute("type")
    public JwtType getType() {
        return type;
    }
    @DynamoDbAttribute("revoked_at")
    public Instant getRevokedAt() {
        return revokedAt;
    }

    @DynamoDbAttribute("reason")
    public String getReason() {
        return reason;
    }

    @DynamoDbAttribute("account_id")
    public String getAccountId() {
        return accountId;
    }
}
