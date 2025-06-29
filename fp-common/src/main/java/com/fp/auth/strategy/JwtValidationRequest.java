package com.fp.auth.strategy;

import com.fp.enumeration.jwt.JwtType;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class JwtValidationRequest {
    private final Jwt jwt;

    private final String requestURI;

    private final JwtType jwtType;

    private final ValidationLevel validationLevel;

    public enum ValidationLevel {
        TYPE_ONLY,          // Only validate JWT type against URI
        BASIC_VALIDATION,   // Type + basic claims validation
        FULL_VALIDATION     // Complete validation including custom rules
    }
    public JwtValidationRequest(Jwt jwt, String requestURI, JwtType jwtType, ValidationLevel validationLevel) {
        this.jwt = jwt;
        this.requestURI = requestURI;
        this.jwtType = jwtType;
        this.validationLevel = validationLevel;
    }

}
