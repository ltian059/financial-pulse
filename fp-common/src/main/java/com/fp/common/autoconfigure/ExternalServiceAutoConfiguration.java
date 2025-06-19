package com.fp.common.autoconfigure;

import com.fp.common.properties.ExternalServiceProperties;
import com.fp.common.util.WebClientFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties(ExternalServiceProperties.class)
@ConditionalOnClass(WebClient.class)
public class ExternalServiceAutoConfiguration {

    private final ExternalServiceProperties properties;

    public ExternalServiceAutoConfiguration(ExternalServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "restTemplate")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "external-services.follow-service",
            name = "enabled", havingValue = "true", matchIfMissing = true
    )
    @ConditionalOnMissingBean(name = "followWebClient")
    public WebClient followWebClient() {
        return WebClientFactory.create(
                properties.getFollowService().getUrl(),
                properties.getFollowService().isEnableJwtPropagation()
        );
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "external-services.content-service",
            name = "enabled", havingValue = "true", matchIfMissing = true
    )
    @ConditionalOnMissingBean(name = "contentWebClient")
    public WebClient contentWebClient() {
        return WebClientFactory.create(
                properties.getContentService().getUrl(),
                properties.getContentService().isEnableJwtPropagation()
        );
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "external-services.account-service",
            name = "enabled", havingValue = "true", matchIfMissing = true
    )
    @ConditionalOnMissingBean(name = "accountWebClient")
    public WebClient accountWebClient(){
        return WebClientFactory.create(
                properties.getAccountService().getUrl(),
                properties.getAccountService().isEnableJwtPropagation()
        );
    }
}
