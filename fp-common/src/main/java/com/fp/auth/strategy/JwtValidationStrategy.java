package com.fp.auth.strategy;

import com.fp.constant.JwtClaimsKey;
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

    JwtValidationResult validateJwt(Jwt jwt, String requestURI);

    JwtValidationResult validateJwtType(JwtType jwtType, String requestURI);

    default JwtValidationResult validateJwtWithLevel(Jwt jwt, String requestURI, JwtValidationRequest.ValidationLevel validationLevel) {
        return switch (validationLevel) {
            case TYPE_ONLY -> {
                JwtType type = JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE));
                yield validateJwtType(type, requestURI);
            }
            case FULL_VALIDATION -> validateJwt(jwt, requestURI);
            default -> validateJwt(jwt, requestURI);
        };
    }

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
        return validateJwtWithLevel(input.getJwt(), input.getRequestURI(), input.getValidationLevel());
    }

}
