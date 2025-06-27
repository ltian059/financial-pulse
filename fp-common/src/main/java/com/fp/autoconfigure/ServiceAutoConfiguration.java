package com.fp.autoconfigure;

import com.fp.properties.ServiceProperties;
import com.fp.util.WebClientFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties(ServiceProperties.class)
@ConditionalOnClass(WebClient.class)
public class ServiceAutoConfiguration {

    private final ServiceProperties properties;

    public ServiceAutoConfiguration(ServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "restTemplate")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(name = "followWebClient")
    @ConditionalOnProperty(prefix = "services.follow", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebClient followWebClient() {
        return WebClientFactory.create(
                properties.getFollow().getUrl(),
                properties.getFollow().isEnableJwtPropagation()
        );
    }

    @Bean
    @ConditionalOnMissingBean(name = "contentWebClient")
    @ConditionalOnProperty(prefix = "services.content", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebClient contentWebClient() {
        return WebClientFactory.create(
                properties.getContent().getUrl(),
                properties.getContent().isEnableJwtPropagation()
        );
    }

    @Bean
    @ConditionalOnMissingBean(name = "accountWebClient")
    @ConditionalOnProperty(prefix = "services.account", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebClient accountWebClient(){
        return WebClientFactory.create(
                properties.getAccount().getUrl(),
                properties.getAccount().isEnableJwtPropagation()
        );
    }
}
