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
///@apiNote This interface is part of the strategy pattern for handling JWT post-validation.
///
public interface JwtValidationStrategy extends Strategy<JwtValidationRequest, JwtValidationResult> {

    /**
     * The JWT should already be validated by JwtDecoder of expiration, signature, etc.
     * <p>
     *
     * Post-validation method for JWTs includes type validation, revocation checks, etc.
     */
    JwtValidationResult postValidateJwt(JwtValidationRequest jwtValidationRequest);


    /**
     * Executes the JWT validation strategy.
     * @param input the input for the strategy
     * @return
     */
    @Override
    default JwtValidationResult execute(JwtValidationRequest input){
        return postValidateJwt(input);
    }

}
