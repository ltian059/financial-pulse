package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.AbstractJwtValidationStrategy;
import com.fp.auth.strategy.JwtValidationRequest;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.constant.JwtClaimsKey;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import static com.fp.util.HttpUtil.isRefreshTokenPath;

@StrategyComponent(
        value = "refreshTokenValidationStrategy",
        description = "Strategy for validating refresh tokens.",
        priority = 1
)

public class RefreshTokenValidationStrategy extends AbstractJwtValidationStrategy {


    @Override
    protected JwtValidationResult validateJwt(JwtValidationRequest jwtValidationRequest) {
        Jwt jwt = jwtValidationRequest.getJwt();
        JwtType jwtType = JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE));
        String requestURI = jwtValidationRequest.getRequestURI();
        if (JwtType.REFRESH.equals(jwtType) && isRefreshTokenPath(requestURI)) {
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.REFRESH_TOKEN_NOT_ALLOWED_ON_PATH + requestURI, HttpStatus.FORBIDDEN);
    }

    @Override
    public boolean supports(JwtValidationRequest type) {
        Jwt jwt = type.getJwt();
        JwtType jwtType = JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE));
        return jwtType.equals(JwtType.REFRESH);
    }


    @Override
    public String getStrategyName() {
        return "refreshTokenValidationStrategy";
    }




}
