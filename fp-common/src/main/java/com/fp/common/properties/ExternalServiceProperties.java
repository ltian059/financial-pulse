package com.fp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external-services")
@Data
public class ExternalServiceProperties {
    private ServiceConfig accountService = new ServiceConfig("http://localhost:8080");
    private ServiceConfig contentService = new ServiceConfig("http://localhost:8081");
    private ServiceConfig followService = new ServiceConfig("http://localhost:8082");

    @Data
    public static class ServiceConfig{
        private String url;
        private boolean enabled = true;
        private int timeout = 5000;
        private int maxRetries = 3;
        public ServiceConfig(){}
        public ServiceConfig(String url) {
            this.url = url;
        }
    }
}
