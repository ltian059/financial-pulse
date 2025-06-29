package com.fp.auth.strategy;

import com.fp.auth.strategy.core.JwtValidationResult;
import com.fp.auth.strategy.core.JwtValidationStrategy;
import com.fp.constant.JwtClaimsKey;
import com.fp.constant.Messages;
import com.fp.constant.UrlConstant;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;

import static com.fp.util.HttpUtil.isRefreshTokenPath;

@StrategyComponent(
        value = "refreshTokenValidationStrategy",
        description = "Strategy for validating refresh tokens.",
        priority = 1
)
public class RefreshTokenValidationStrategy implements JwtValidationStrategy {
    @Override
    public boolean supportsJwtType(JwtType jwtType) {
        return JwtType.REFRESH.equals(jwtType);
    }

    @Override
    public JwtValidationResult validateJwt(Jwt jwt, String requestUri) {
        String claimAsString = jwt.getClaimAsString(JwtClaimsKey.TYPE);
        JwtType type;
        try {
            type = JwtType.fromString(claimAsString);
            if (JwtType.REFRESH.equals(type) && isRefreshTokenPath(requestUri)) {
                return JwtValidationResult.success();
            }
            return JwtValidationResult.failure(Messages.Error.Auth.REFRESH_TOKEN_NOT_ALLOWED_ON_PATH + requestUri, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return JwtValidationResult.failure(Messages.Error.Auth.INVALID_TOKEN_TYPE + claimAsString, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }



}
