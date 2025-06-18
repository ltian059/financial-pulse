package com.fp.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    /**
//     * remove default login authentication
//     * @param http
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth ->
//                        auth.anyRequest().permitAll() // Allow all requests without authentication
//                )
//                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for simplicity, not recommended for production
//                .formLogin(formLogin -> formLogin.disable()) // Disable form login
//                .httpBasic(httpBasicAuth -> httpBasicAuth.disable()); // Disable HTTP Basic authentication
//
//        return http.build();
//    }
}
