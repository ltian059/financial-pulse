package com.fp.auth.strategy;

import com.fp.enumeration.jwt.JwtType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
@Builder
public class JwtValidationRequest {
    private final Jwt jwt;

    private final String requestURI;


}
