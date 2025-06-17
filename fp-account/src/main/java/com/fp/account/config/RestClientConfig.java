package com.fp.account.config;

import com.fp.config.ExternalServiceConfig;
import com.fp.util.WebClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(ExternalServiceConfig.class)
public class RestClientConfig {
    @Autowired
    private ExternalServiceConfig externalServiceConfig;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        //customize the RestTemplate if needed
        return restTemplate;
    }

    @Bean
    public WebClient followWebClient() {
        return WebClientFactory.create(externalServiceConfig.getFollowService().getUrl());
    }

    @Bean
    public WebClient contentWebClient() {
        return WebClientFactory.create(externalServiceConfig.getContentService().getUrl());
    }


}
