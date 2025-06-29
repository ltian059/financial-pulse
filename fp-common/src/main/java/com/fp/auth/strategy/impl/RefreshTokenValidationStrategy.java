package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.AbstractJwtValidationStrategy;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.auth.strategy.JwtValidationStrategy;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.RevokedJwtRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import static com.fp.util.HttpUtil.isRefreshTokenPath;

@StrategyComponent(
        value = "refreshTokenValidationStrategy",
        description = "Strategy for validating refresh tokens.",
        priority = 1
)
public class RefreshTokenValidationStrategy extends AbstractJwtValidationStrategy {
    public RefreshTokenValidationStrategy(RevokedJwtRepository revokedJwtRepository) {
        super(revokedJwtRepository);
    }

    @Override
    public boolean supportsJwtType(JwtType jwtType) {
        return JwtType.REFRESH.equals(jwtType);
    }

    @Override
    public JwtValidationResult validateJwtType(JwtType jwtType, String requestUri) {

        if (JwtType.REFRESH.equals(jwtType) && isRefreshTokenPath(requestUri)) {
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.REFRESH_TOKEN_NOT_ALLOWED_ON_PATH + requestUri, HttpStatus.FORBIDDEN);

    }

    @Override
    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }



}
