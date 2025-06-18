package com.fp.common.properties;

import com.fp.common.constant.JwtPropertiesConstant;
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
    private String issuer = JwtPropertiesConstant.JWT_ISSUER;

    /**
     * JWT Audience
     */
    private String audience = JwtPropertiesConstant.JWT_AUDIENCE;

    private TokenConfig accessToken = new TokenConfig();
    private TokenConfig refreshToken = new TokenConfig();
    {
        accessToken.setHeaderName(JwtPropertiesConstant.JWT_ACCESS_TOKEN_HEADER_NAME);
        accessToken.setExpiration(Duration.ofHours(JwtPropertiesConstant.JWT_ACCESS_TOKEN_EXPIRATION));

        refreshToken.setExpiration(Duration.ofHours(JwtPropertiesConstant.JWT_REFRESH_TOKEN_EXPIRATION));
        refreshToken.setHeaderName(JwtPropertiesConstant.JWT_REFRESH_TOKEN_HEADER_NAME);
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
        private String prefix = JwtPropertiesConstant.JWT_PREFIX;

        /**
         * JWT Token Header
         */
        private String headerName;

        public long getExpirationInMillis() {
            return expiration.toMillis();
        }
        public long getExpirationInSeconds() {
            return expiration.getSeconds();
        }
    }

}
