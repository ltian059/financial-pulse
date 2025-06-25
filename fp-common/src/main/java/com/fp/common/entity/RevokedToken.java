package com.fp.common.entity;

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
public class RevokedToken {

    /**
     * JWT "jti" (JWT ID) claim, which is a unique identifier for the token.
     */
    private String tokenId;
    /**
     * The time when the token was revoked.
     */
    private Instant revokedAt;
    /**
     * The original expiration time of the token.
     */
    private Long ttl;

    private String reason;

    @DynamoDbPartitionKey
    public String getTokenId() {
        return tokenId;
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

}
