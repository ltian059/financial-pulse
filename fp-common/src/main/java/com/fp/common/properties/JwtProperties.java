package com.fp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


@ConfigurationProperties(prefix = "fp.jwt")
@Data
public class JwtProperties {

    /**
     * JWT signature secret key
     */
    private String secret;

    /**
     * Enable or disable JWT authentication
     */
    private Boolean enabled = true;

    private String defaultIssuer = "financial-pulse-issuer";

    private TokenConfig accessTokenConfig = new TokenConfig();
    private TokenConfig refreshTokenConfig = new TokenConfig();

    public JwtProperties() {
        // Default values for JWT tokens
        accessTokenConfig.setExpiration(Duration.ofHours(24));
        accessTokenConfig.setPrefix("Bearer ");
        accessTokenConfig.setAudience("financial-pulse-api");
        accessTokenConfig.setType("access");
        accessTokenConfig.setIssuer(defaultIssuer);

        refreshTokenConfig.setExpiration(Duration.ofDays(7));
        refreshTokenConfig.setPrefix("Bearer ");
        refreshTokenConfig.setAudience("financial-pulse-refresh");
        refreshTokenConfig.setIssuer(defaultIssuer);
        refreshTokenConfig.setType("refresh");
    }
    /**
     * JWT Token Configuration
     */
    @Data
    public static class TokenConfig {
        /**
         * JWT Token Expiration time
         */
        private Duration expiration;

        /**
         * JWT Token Prefix
         */
        private String prefix;

        private String issuer;
        /**
         * Token-specific audience - defines what this token can access
         */
        private String audience;

        /**
         * Token type identifier (access, refresh, etc.)
         */
        private String type;

        public long getExpirationInMillis() {
            return expiration.toMillis();
        }
        public long getExpirationInSeconds() {
            return expiration.getSeconds();
        }
    }

}
