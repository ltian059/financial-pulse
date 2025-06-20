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

    /**
     * JWT Issuer
     */
    private String issuer;

    /**
     * JWT Audience
     */
    private String audience;

    private TokenConfig accessToken = new TokenConfig();
    private TokenConfig refreshToken = new TokenConfig();


    public JwtProperties() {
        // Default values for JWT tokens
        accessToken.setExpiration(Duration.ofHours(24));
        accessToken.setPrefix("Bearer ");
        refreshToken.setExpiration(Duration.ofDays(7));
        refreshToken.setPrefix("Bearer ");

        setIssuer("financial-pulse-issuer");
        setAudience("financial-pulse-api");
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


        public long getExpirationInMillis() {
            return expiration.toMillis();
        }
        public long getExpirationInSeconds() {
            return expiration.getSeconds();
        }
    }

}
