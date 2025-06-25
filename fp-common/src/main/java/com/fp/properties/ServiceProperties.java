package com.fp.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
@Data
public class ServiceProperties {
    private ServiceConfig account;
    private ServiceConfig content;
    private ServiceConfig follow;

    public ServiceProperties() {
        account = ServiceConfig.builder()
                .url("http://localhost:8080")
                .build();
        content = ServiceConfig.builder()
                .url("http://localhost:8081")
                .build();
        follow = ServiceConfig.builder()
                .url("http://localhost:8082")
                .build();
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServiceConfig{
        private String url;
        private boolean enabled = true;
        /**
         * Allows other services which are using this service to propagate JWT tokens to this service.
         */
        private boolean enableJwtPropagation = true;
        private int timeout = 5000;
        private int maxRetries = 3;
    }
}
