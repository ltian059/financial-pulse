package com.fp.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
@Data
public class ServiceProperties {
    private ServiceConfig account = new ServiceConfig();
    private ServiceConfig content = new ServiceConfig();
    private ServiceConfig follow = new ServiceConfig();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceConfig {

        private String url;
        private boolean enabled = false;
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
