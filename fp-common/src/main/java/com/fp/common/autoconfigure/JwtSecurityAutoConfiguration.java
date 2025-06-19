package com.fp.common.autoconfigure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.common.auth.CustomAccessDeniedHandler;
import com.fp.common.auth.CustomAuthenticationEntryPoint;
import com.fp.common.constant.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@AutoConfiguration(after = JwtAutoConfiguration.class)
@ConditionalOnClass(JwtDecoder.class)
public class JwtSecurityAutoConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }
    @Bean
    @ConditionalOnMissingBean
    public CustomAccessDeniedHandler customAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CustomAccessDeniedHandler(objectMapper);
    }


    /**
     * Default SecurityFilterChain
     * Can be Overridden by defining a bean with the same name.
     * @param http
     * @param jwtDecoder
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultJwtSecurityFilterChain")
    public SecurityFilterChain defaultJwtSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler
    ) throws Exception {
        log.info(" Creating default JWT SecurityFilterChain");
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(UrlConstant.ALLOWED_REQUEST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                // OAuth2 Resource Server configure - specialized in JWT exception handling
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // Global security exception handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .build();
    }
}
