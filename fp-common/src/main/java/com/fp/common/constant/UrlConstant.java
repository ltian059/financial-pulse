package com.fp.common.constant;

public class UrlConstant {
    /**
     * Urls that are allowed to be accessed without authentication.
     */
    public static final String[] ALLOWED_REQUEST_URLS = {
            "/api/auth/**",
            "swagger-ul/**", "/v3/api-docs/**", "/swagger-ui/**"
    };
}
