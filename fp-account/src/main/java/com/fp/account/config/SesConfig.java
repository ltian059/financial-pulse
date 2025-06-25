package com.fp.account.config;

import com.fp.account.properties.SesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.services.ses.SesClient;

/// Email configuration for AWS SES in the account service.
///

@Configuration
@EnableConfigurationProperties(SesProperties.class)
@ConditionalOnProperty(name = "fp.aws.ses.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class SesConfig {
    private final SesProperties sesProperties;

    @Bean
    public SesClient sesClient(){
        return SesClient.builder()
                .region(sesProperties.getAwsRegion())
                .build();
    }
}
