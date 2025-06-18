package com.fp.common.config;

import com.fp.common.properties.JwtProperties;
import com.fp.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

@EnableConfigurationProperties(JwtProperties.class)
@AutoConfiguration
@Slf4j
@ConditionalOnProperty(prefix = "fp.jwt", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(JwtDecoder.class)
public class JwtAutoConfiguration {
    private final JwtProperties jwtProperties;

    /**
     * Constructor for JwtAutoConfiguration.
     * Auto-injects JwtProperties and logs the configuration.
     *
     * @param jwtProperties
     */
    public JwtAutoConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Creates a JwtUtil bean if one is not already defined.
     * This bean will be used for JWT operations such as token generation and validation.
     *
     * @return JwtUtil instance
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtProperties);
    }


    /**
     * Spring Security OAuth2 JWT Decoder bean.
     * HMAC: Hash-based Message Authentication Code
     * @Details: When a request arrives, JwtDecoder will:
     * @1. Break down JWT: Header.Payload.Signature
     * @2. Use the same secret key to calculate the signature.
     * @3. Compare the calculated signature with the one in the JWT.
     * @4. Verify the expiration time.
     * @5. Extract User information from the JWT.
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(),
                "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }



}
