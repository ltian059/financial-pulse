package com.fp.common.properties;

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

    private String tablePrefix = "fp";

    private String tableSuffix = "dev";

    public Region getAwsRegion() {
        return Region.of(region);
    }
    public String getFullTableName(String tableName){
        return String.format("%s-%s-%s", tablePrefix, tableName, tableSuffix);
    }
}
