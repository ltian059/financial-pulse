package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.JwtValidationResult;
import com.fp.auth.strategy.JwtValidationStrategy;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import static com.fp.util.HttpUtil.isVerificationTokenPath;

@StrategyComponent(
        value = "verificationTokenValidationStrategy",
        description = "Strategy to validate verification tokens",
        priority = 1
)
public class VerificationTokenValidationStrategy implements JwtValidationStrategy {
    @Override
    public boolean supportsJwtType(JwtType jwtType) {
        return JwtType.VERIFICATION.equals(jwtType);
    }

    @Override
    public JwtValidationResult validateJwt(Jwt jwt, String requestURI) {
        //TODO add specific validation logic for verification tokens if needed.
        return null;
    }

    @Override
    public JwtValidationResult validateJwtType(JwtType type, String requestUri) {
        if(JwtType.VERIFICATION.equals(type) && isVerificationTokenPath(requestUri)) {
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.VERIFICATION_TOKEN_NOT_ALLOWED_ON_PATH + requestUri, HttpStatus.FORBIDDEN);

    }

    @Override
    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }

}
