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
        this.account = new ServiceConfig("http://localhost:8080");
        this.content = new ServiceConfig("http://localhost:8081");
        this.follow = new ServiceConfig("http://localhost:8082");
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceConfig{

        private String url;
        private boolean enabled = true;
        /**
         * Allows other services which are using this service to propagate JWT tokens to this service.
         */
        private boolean enableJwtPropagation = true;
        private int timeout = 5000;
        private int maxRetries = 3;

        public ServiceConfig(String url) {
            this.url = url;
        }
    }
}
