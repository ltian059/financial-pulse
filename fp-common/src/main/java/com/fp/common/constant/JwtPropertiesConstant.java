package com.fp.common.constant;

public class JwtPropertiesConstant {
    public static final String JWT_ISSUER = "financial-pulse-issuer";

    public static final String JWT_AUDIENCE = "financial-pulse-api";

    public static final String JWT_PREFIX = "Bearer ";

    public static final String JWT_ACCESS_TOKEN_HEADER_NAME = "Authorization";

    public static final String JWT_REFRESH_TOKEN_HEADER_NAME = "Refresh-Token";

    public static final Integer JWT_ACCESS_TOKEN_EXPIRATION = 24; // hours

    public static final Integer JWT_REFRESH_TOKEN_EXPIRATION = 7 * 24; // hours, default 7 days
}
