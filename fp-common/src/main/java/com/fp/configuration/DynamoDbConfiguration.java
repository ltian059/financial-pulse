package com.fp.configuration;

import com.fp.properties.DynamoDbProperties;
import com.fp.dynamodb.repository.RevokedJwtRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
@Configuration
@EnableConfigurationProperties(DynamoDbProperties.class)
@ConditionalOnClass({DynamoDbClient.class, DynamoDbEnhancedClient.class})
@ConditionalOnProperty(prefix = "fp.aws.dynamodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class DynamoDbConfiguration {
    private final DynamoDbProperties dynamoDbProperties;

    public DynamoDbConfiguration(DynamoDbProperties dynamoDbProperties) {
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
    public RevokedJwtRepository revokedJwtRepository() {
        return new RevokedJwtRepository();
    }




}
