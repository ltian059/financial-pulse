package com.fp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .authorities("ROLE_ADMIN", "ADMIN")  // 可以设置多个权限
                .build();

        UserDetails dev = User.builder()
                .username("developer")
                .password(passwordEncoder().encode("dev2024!"))
                .roles("USER")
                .authorities("ROLE_USER", "USER")
                .build();

        UserDetails viewer = User.builder()
                .username("viewer")
                .password(passwordEncoder().encode("view123"))
                .roles("VIEWER")
                .authorities("ROLE_VIEWER")
                .accountLocked(false)           // 账户未锁定
                .accountExpired(false)          // 账户未过期
                .credentialsExpired(false)      // 密码未过期
                .disabled(false)                // 账户未禁用
                .build();

        return new InMemoryUserDetailsManager(admin, dev, viewer);
    }
}
