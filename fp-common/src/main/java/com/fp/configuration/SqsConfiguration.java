package com.fp.configuration;

import com.fp.properties.SqsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@Slf4j
@ConditionalOnProperty(name = "fp.aws.sqs.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(SqsClient.class)
@RequiredArgsConstructor
public class SqsConfiguration {
    private final SqsProperties sqsProperties;


    @Bean
    @ConditionalOnMissingBean
    public SqsClient sqsClient() {
        log.info("Creating SQS Client with region: {}", sqsProperties.getRegion());
        return SqsClient.builder()
                .region(sqsProperties.getAwsRegion())
                .build();
    }
}
