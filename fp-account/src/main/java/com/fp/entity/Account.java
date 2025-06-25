package com.fp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Account {

    private String accountId;

    private String name;

    private String email;

    private String encryptedPassword;

    private LocalDate birthday;

    private Instant createdAt;

    private Instant modifiedAt;

    private String labels;

    private Boolean verified;

    @DynamoDbSecondaryPartitionKey(indexNames = "account-id-index")
    @DynamoDbAttribute("account_id")
    public String getAccountId() {
        return accountId;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("encrypted_password")
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @DynamoDbAttribute("birthday")
    public LocalDate getBirthday() {
        return birthday;
    }

    @DynamoDbAttribute("created_at")
    public Instant getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("modified_at")
    public Instant getModifiedAt() {
        return modifiedAt;
    }

    @DynamoDbAttribute("labels")
    public String getLabels() {
        return labels;
    }

    @DynamoDbAttribute("verified")
    public Boolean getVerified() {
        return verified;
    }
}
