package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.JwtValidationResult;
import com.fp.auth.strategy.JwtValidationStrategy;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import static com.fp.util.HttpUtil.isRefreshTokenPath;
import static com.fp.util.HttpUtil.isVerificationTokenPath;

@StrategyComponent(
        value = "accessTokenValidationStrategy",
        description = "Strategy to validate access tokens",
        priority = 1
)
public class AccessTokenValidationStrategy implements JwtValidationStrategy {
    @Override
    public boolean supportsJwtType(JwtType jwtType) {
        return JwtType.ACCESS.equals(jwtType);
    }

    @Override
    public JwtValidationResult validateJwt(Jwt jwt, String requestURI) {
        //TODO add specific validation logic for access tokens if needed.
        return null;
    }

    @Override
    public JwtValidationResult validateJwtType(JwtType jwtType, String requestURI) {
        // Access token cannot be used for a refreshing or verifying account.
        if(isRefreshTokenPath(requestURI) || isVerificationTokenPath(requestURI)){
            return JwtValidationResult.failure(Messages.Error.Auth.ACCESS_TOKEN_NOT_ALLOWED_ON_PATH, HttpStatus.FORBIDDEN);
        }
        // Potentially, other validations can be added here for access tokens.

        return JwtValidationResult.success();
    }



    @Override
    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }

}
