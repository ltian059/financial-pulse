package com.fp.auth.strategy;

import com.fp.auth.strategy.core.JwtValidationResult;
import com.fp.auth.strategy.core.JwtValidationStrategy;
import com.fp.constant.Messages;
import com.fp.constant.UrlConstant;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;

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
    public JwtValidationResult validateJwt(Jwt jwt, String requestUri) {
        // Access token cannot be used for a refreshing or verifying account.
        if(isRefreshTokenPath(requestUri) || isVerificationTokenPath(requestUri)){
            return JwtValidationResult.failure(Messages.Error.Auth.ACCESS_TOKEN_NOT_ALLOWED_ON_PATH, HttpStatus.FORBIDDEN);
        }
        // Potentially, other validations can be added here for access tokens.

        return JwtValidationResult.success();
    }

    @Override
    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }

    private boolean isRefreshTokenPath(String uri) {
        return Arrays.stream(UrlConstant.REFRESH_TOKEN_ONLY_PATHS)
                .anyMatch(uri::startsWith);
    }
    private boolean isVerificationTokenPath(String uri) {
        return Arrays.stream(UrlConstant.VERIFY_TOKEN_ONLY_PATHS)
                .anyMatch(uri::startsWith);
    }
}
