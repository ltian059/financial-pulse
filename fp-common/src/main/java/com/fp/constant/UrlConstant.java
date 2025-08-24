package com.fp.constant;

public class UrlConstant {
    /**
     * Urls that are allowed to be accessed without authentication.
     */
    public static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "/actuator/health/**"
    };

    public static final String[] PROTECTED_PATHS = {
            "/v3/api-docs/**", "/swagger-ui/**",

    };


    public static final String[] REFRESH_TOKEN_ONLY_PATHS = {
            "/api/auth/refresh"
    };

    public static final String[] VERIFY_TOKEN_ONLY_PATHS = {
            "/api/auth/verify"
    };
}
