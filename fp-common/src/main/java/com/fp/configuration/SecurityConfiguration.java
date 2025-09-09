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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;

@Configuration
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
    @ConditionalOnClass(JwtDecoder.class)
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
        log.info("Creating webSecurityFilterChain for non-API paths");
        return http
                .securityMatcher(request -> {
                    String uri = request.getRequestURI();
                    boolean matches = !uri.startsWith("/api/");
                    return matches;
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/*/health").permitAll()
                            .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/actuator/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                            .anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll()
                )
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.permitAll())
                .build();
    }

    /**
     * Default SecurityFilterChain
     * Can be Overridden by defining a bean with the same name.
     *
     * @param http       HttpSecurity instance
     * @param jwtDecoder JwtDecoder instance for decoding JWT tokens
     * @return SecurityFilterChain
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultJwtSecurityFilterChain")
    @ConditionalOnClass(JwtDecoder.class)
    @Order(2)
    public SecurityFilterChain defaultJwtSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtTypeValidationFilter jwtTypeValidationFilter
    ) throws Exception {
        log.info("Creating defaultJwtSecurityFilterChain for API paths");
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

    @Bean
    public UserDetailsService userDetailsService() throws IOException {
        String randomPasswd = generateRandomPassword(16);
        // Save the generated password to a file
        Files.write(
                Paths.get("user-passwords.txt"),
                ("admin=" + randomPasswd + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode(randomPasswd))
                .roles("ADMIN")
                .authorities("ROLE_ADMIN", "ADMIN")  // 可以设置多个权限
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
    private String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*()-_=+[]{}<>?";
        String all = upper + lower + digits + specials;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(all.length());
            sb.append(all.charAt(index));
        }
        return sb.toString();
    }

}
