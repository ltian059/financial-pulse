package com.fp.common.autoconfigure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.common.auth.CustomAccessDeniedHandler;
import com.fp.common.auth.CustomAuthenticationEntryPoint;
import com.fp.common.auth.JwtTokenTypeValidationFilter;
import com.fp.common.constant.UrlConstant;
import com.fp.common.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@AutoConfiguration(after = JwtAutoConfiguration.class)
@ConditionalOnClass(JwtDecoder.class)
public class SecurityAutoConfiguration {
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

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenTypeValidationFilter jwtTokenTypeValidationFilter(ObjectMapper objectMapper) {
        return new JwtTokenTypeValidationFilter(objectMapper);
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
    @Order(1)
    public SecurityFilterChain defaultJwtSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtTokenTypeValidationFilter jwtTokenTypeValidationFilter
    ) throws Exception {
        return http
                .securityMatcher("/api/**") // Only Match all API paths
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
                .addFilterAfter(jwtTokenTypeValidationFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

    /**
     * 默认 SecurityFilterChain 处理非 API 路径
     * 优先级较低，处理所有其他路径
     */
    @Bean
    @ConditionalOnMissingBean(name = "formLoginSecurityFilterChain")
    @Order(10)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Creating default SecurityFilterChain for non-API paths");
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpUtil::isLocalhostRequest).permitAll()
                        .anyRequest().authenticated()  // 所有其他路径都需要认证
                )
                .formLogin(form -> form
                        // 使用默认登录页面
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                )
                .csrf(csrf -> csrf.disable())
                .build();
    }

}
