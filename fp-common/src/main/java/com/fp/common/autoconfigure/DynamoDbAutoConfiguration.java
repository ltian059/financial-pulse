package com.fp.common.autoconfigure;

import com.fp.common.properties.DynamoDbProperties;
import com.fp.common.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@AutoConfiguration
@EnableConfigurationProperties(DynamoDbProperties.class)
@ConditionalOnClass({DynamoDbClient.class, DynamoDbEnhancedClient.class})
@ConditionalOnProperty(prefix = "fp.aws.dynamodb", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class DynamoDbAutoConfiguration {
    private final DynamoDbProperties dynamoDbProperties;

    public DynamoDbAutoConfiguration(DynamoDbProperties dynamoDbProperties) {
        this.dynamoDbProperties = dynamoDbProperties;
        log.info("DynamoDbAutoConfiguration initialized");
    }


    @Bean
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient() {
       return DynamoDbClient.builder()
                .region(dynamoDbProperties.getAwsRegion())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient dbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        return dbEnhancedClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbService dynamoDbService(DynamoDbClient dynamoDbClient, DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return new DynamoDbService(dynamoDbClient, dynamoDbEnhancedClient, dynamoDbProperties);
    }



}
