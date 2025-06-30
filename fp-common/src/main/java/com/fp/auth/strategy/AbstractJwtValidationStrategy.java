package com.fp.auth.strategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractJwtValidationStrategy implements JwtValidationStrategy {

    protected abstract JwtValidationResult validateJwt(JwtValidationRequest jwtValidationRequest);

    @Override
    public JwtValidationResult postValidateJwt(JwtValidationRequest jwtValidationRequest) {
        //validate the JWT type based on the implemented strategies.
        JwtValidationResult jwtValidationResult = validateJwt(jwtValidationRequest);
        if (!jwtValidationResult.isValid()) {
            return jwtValidationResult;
        }
        //3. If the JWT is valid and not revoked, return success.
        return JwtValidationResult.success();

    }

}
