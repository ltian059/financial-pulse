package com.fp.common.constant;

public class UrlConstant {
    /**
     * Urls that are allowed to be accessed without authentication.
     */
    public static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "swagger-ul/**", "/v3/api-docs/**", "/swagger-ui/**",
            "/debug/**"
    };

    public static final String[] REFRESH_TOKEN_ONLY_PATHS = {
            "/api/auth/refresh"
    };
}
