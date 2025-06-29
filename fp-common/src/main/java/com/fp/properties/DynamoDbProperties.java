package com.fp.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

@ConfigurationProperties(prefix = "fp.aws.dynamodb")
@Data
@Validated
public class DynamoDbProperties {
    /**
     * Enable or disable DynamoDB integration; default is false
     */
    @NotNull(message = "DynamoDB enabled must be specified")
    private final boolean enabled;

    /**
     * AWS Region for DynamoDB
     */
    @NotBlank(message = "AWS region must be specified when DynamoDB is enabled")
    private final String region;

    /**
     * Table prefix for DynamoDB tables
     * e.g: if tablePrefix == fp, suffix is dev, whole table name is fp-tableName-dev
     */
    @NotBlank(message = "DynamoDB table suffix must be specified when enabled")
    private final String tablePrefix;

    /**
     * Table suffix for DynamoDB tables
     * e.g: fp-tableName-dev
     */
    @NotBlank(message = "DynamoDB table suffix must be specified when enabled")
    private final String tableSuffix;

    public Region getAwsRegion() {
        return Region.of(region);
    }
    public String getFullTableName(String tableName){
        return String.format("%s-%s-%s", tablePrefix, tableName, tableSuffix);
    }
}
