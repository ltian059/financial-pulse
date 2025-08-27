package com.fp.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.auth.CustomAccessDeniedHandler;
import com.fp.auth.CustomAuthenticationEntryPoint;
import com.fp.auth.filter.JwtTypeValidationFilter;
import com.fp.auth.strategy.JwtValidationContext;
import com.fp.constant.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
@ConditionalOnClass(JwtDecoder.class)
@Order(2)
public class SecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
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

    @Bean
    @ConditionalOnMissingBean
    public JwtTypeValidationFilter jwtTokenTypeValidationFilter(ObjectMapper objectMapper, JwtValidationContext jwtValidationContext) {
        return new JwtTypeValidationFilter(objectMapper, jwtValidationContext);
    }

    /**
     * Web SecurityFilterChain for Swagger, login and static resources
     * Handles non-API paths with form-based authentication
     */
    @Bean
    @ConditionalOnMissingBean(name = "webSecurityFilterChain")
    @Order(1)
    public SecurityFilterChain webSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {
        return http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/api/"))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/health", "/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .build();
    }

    /**
     * Default SecurityFilterChain
     * Can be Overridden by defining a bean with the same name.
     * @param http HttpSecurity instance
     * @param jwtDecoder JwtDecoder instance for decoding JWT tokens
     * @return SecurityFilterChain
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultJwtSecurityFilterChain")
    @Order(2)
    public SecurityFilterChain defaultJwtSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtTypeValidationFilter jwtTypeValidationFilter
    ) throws Exception {
        return http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(UrlConstant.PUBLIC_PATHS).permitAll()
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
                // Add JWT token type validation filter after OAuth2 authentication
                .addFilterAfter(jwtTypeValidationFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }


}
