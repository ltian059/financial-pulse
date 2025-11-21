package com.fp.auth.strategy;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
@Builder
public class JwtValidationRequest {
    private final Jwt jwt;

    private final String requestURI;


}
