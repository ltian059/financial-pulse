package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.AbstractJwtValidationStrategy;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.RevokedJwtRepository;
import org.springframework.http.HttpStatus;

import static com.fp.util.HttpUtil.isRefreshTokenPath;
import static com.fp.util.HttpUtil.isVerificationTokenPath;

@StrategyComponent(
        value = "accessTokenValidationStrategy",
        description = "Strategy to validate access tokens",
        priority = 1
)
public class AccessTokenValidationStrategy extends AbstractJwtValidationStrategy {
    public AccessTokenValidationStrategy(RevokedJwtRepository revokedJwtRepository) {
        super(revokedJwtRepository);
    }

    @Override
    public boolean supportsJwtType(JwtType jwtType) {
        return JwtType.ACCESS.equals(jwtType);
    }

    @Override
    protected JwtValidationResult validateJwtType(JwtType jwtType, String requestUri) {
        if(isRefreshTokenPath(requestUri) || isVerificationTokenPath(requestUri)){
            return JwtValidationResult.failure(Messages.Error.Auth.ACCESS_TOKEN_NOT_ALLOWED_ON_PATH + requestUri, HttpStatus.FORBIDDEN);
        }
        if(jwtType.equals(JwtType.ACCESS)){
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.INVALID_TOKEN_TYPE + jwtType, HttpStatus.UNAUTHORIZED);
    }


    @Override
    public String getStrategyName() {
        return "accessTokenValidationStrategy";
    }
}
