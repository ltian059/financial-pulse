package com.fp.auth.strategy.core;

import com.fp.constant.JwtClaimsKey;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.core.strategy.StrategyContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

///
/// # Context for JWT validation strategies.
///
/// This class extends the `StrategyContext` to provide a context for JWT validation strategies.
///
/// Use for managing the execution of different JWT validation strategies based on the request.
@Component
public class JwtValidationContext extends StrategyContext<JwtValidationRequest, JwtValidationResult> {

    public JwtValidationResult validateJwtType(Jwt jwt, String requestUri, JwtValidationRequest.ValidationLevel validationLevel) {
        //1. Get the JwtType from the JWT claims.
        String typeString = jwt.getClaimAsString(JwtClaimsKey.TYPE);
        JwtType type;
        try {
            type = JwtType.fromString(typeString);
        } catch (IllegalArgumentException e) {
            return JwtValidationResult.failure("Invalid JWT type: " + typeString, HttpStatus.UNAUTHORIZED);
        }
        //2. Create a JwtValidationRequest with the JWT and request URI.
        var request = new JwtValidationRequest(jwt, requestUri, type, validationLevel);

        return super.executeStrategy(request);
    }

}
