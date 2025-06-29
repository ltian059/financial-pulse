package com.fp.auth.strategy.core;

import com.fp.enumeration.jwt.JwtType;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class JwtValidationRequest {
    private final Jwt jwt;

    private final String requestURI;

    private final JwtType jwtType;

    public JwtValidationRequest(Jwt jwt, String requestURI, JwtType jwtType) {
        this.jwt = jwt;
        this.requestURI = requestURI;
        this.jwtType = jwtType;
    }

}
