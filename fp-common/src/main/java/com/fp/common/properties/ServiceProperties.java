package com.fp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
@Data
public class ServiceProperties {
    private ServiceConfig account = new ServiceConfig("http://localhost:8080");
    private ServiceConfig content = new ServiceConfig("http://localhost:8081");
    private ServiceConfig follow = new ServiceConfig("http://localhost:8082");

    @Data
    public static class ServiceConfig{
        private String url;
        private boolean enabled = true;
        /**
         * Allows other services which are using this service to propagate JWT tokens to this service.
         */
        private boolean enableJwtPropagation = true;
        private int timeout = 5000;
        private int maxRetries = 3;
        public ServiceConfig(){}
        public ServiceConfig(String url) {
            this.url = url;
        }
    }
}
