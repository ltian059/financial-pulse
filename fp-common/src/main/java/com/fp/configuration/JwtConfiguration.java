package com.fp.configuration;

import com.fp.properties.JwtProperties;
import com.fp.service.JwtService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
@ConditionalOnProperty(prefix = "fp.jwt", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(JwtDecoder.class)
@Order(1)
public class JwtConfiguration {
    private final JwtProperties jwtProperties;
    /**
     * Constructor for JwtAutoConfiguration.
     * Auto-injects JwtProperties and logs the configuration.
     *
     * @param jwtProperties
     */
    public JwtConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }



    ///      Spring Security OAuth2 JWT Decoder bean.
    ///      HMAC: Hash-based Message Authentication Code
    ///      Details: When a request arrives, JwtDecoder will:
    ///      1. Break down JWT: Header.Payload.Signature
    ///      2. Use the same secret key to calculate the signature.
    ///      3. Compare the calculated signature with the one in the JWT.
    ///      4. Verify the expiration time.
    ///      5. Extract User information from the JWT.
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

    @Bean
    @ConditionalOnMissingBean
    public JwtEncoder jwtEncoder() {
        SecretKeySpec secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(),
                "HmacSHA256"
        );
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }


    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtProperties jwtProperties) {
        return new JwtService(jwtEncoder, jwtDecoder, jwtProperties);
    }

}
