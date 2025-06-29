package com.fp.auth.strategy.core;

import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.core.strategy.Strategy;
import org.springframework.security.oauth2.jwt.Jwt;

///
/// # Interface for JWT validation strategies.
///
///Implementations of this interface should provide specific JWT validation logic.
///
///@apiNote This interface is part of the strategy pattern for handling JWT validation.
///
public interface JwtValidationStrategy extends Strategy<JwtValidationRequest, JwtValidationResult> {

    boolean supportsJwtType(JwtType jwtType);

    JwtValidationResult validateJwt(Jwt jwt, String requestUri);

    /**
     * Default method to implement the method of Strategy interface.
     * @param input
     * @return
     */
    @Override
    default boolean supports(JwtValidationRequest input) {
        return supportsJwtType(input.getJwtType());
    }

    /**
     * Executes the JWT validation strategy.
     * @param input the input for the strategy
     * @return
     */
    @Override
    default JwtValidationResult execute(JwtValidationRequest input){
        return validateJwt(input.getJwt(), input.getRequestURI());
    }
}
