package com.fp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "external-services")
@Data
public class ExternalServiceConfig {
    private ServiceUrl contentService;
    private ServiceUrl followService;
    private ServiceUrl accountService;

    @Data
    public static class ServiceUrl{
        private String url;
    }
}
