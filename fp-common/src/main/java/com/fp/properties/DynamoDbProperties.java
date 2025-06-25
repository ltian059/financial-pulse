package com.fp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

@ConfigurationProperties(prefix = "fp.aws.dynamodb")
@Data
public class DynamoDbProperties {
    /**
     * Enable or disable DynamoDB integration; default is false
     */
    private boolean enabled;

    /**
     * AWS Region for DynamoDB
     */
    private String region;

    /**
     * Table prefix for DynamoDB tables
     * e.g: if tablePrefix == fp, suffix is dev, whole table name is fp-tableName-dev
     */
    private String tablePrefix;

    /**
     * Table suffix for DynamoDB tables
     * e.g: fp-tableName-dev
     */
    private String tableSuffix;

    public Region getAwsRegion() {
        return Region.of(region);
    }
    public String getFullTableName(String tableName){
        return String.format("%s-%s-%s", tablePrefix, tableName, tableSuffix);
    }
}
