package com.fp.auth.strategy;

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

    public JwtValidationResult executeValidationStrategy(JwtValidationRequest input) {
        return super.executeStrategy(input);
    }
}
