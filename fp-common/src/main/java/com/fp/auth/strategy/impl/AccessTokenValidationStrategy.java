package com.fp.auth.strategy.impl;

import com.fp.auth.strategy.AbstractJwtValidationStrategy;
import com.fp.auth.strategy.JwtValidationRequest;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.constant.JwtClaimsKey;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.pattern.annotation.StrategyComponent;
import org.springframework.http.HttpStatus;

import static com.fp.util.HttpUtil.isRefreshTokenPath;
import static com.fp.util.HttpUtil.isVerificationTokenPath;

@StrategyComponent(
        value = "accessTokenValidationStrategy",
        description = "Strategy to validate access tokens",
        priority = 1
)
public class AccessTokenValidationStrategy extends AbstractJwtValidationStrategy {

    @Override
    protected JwtValidationResult validateJwt(JwtValidationRequest jwtValidationRequest) {
        String requestURI = jwtValidationRequest.getRequestURI();
        JwtType jwtType = JwtType.fromString(jwtValidationRequest.getJwt().getClaimAsString(JwtClaimsKey.TYPE));
        if(isRefreshTokenPath(requestURI) || isVerificationTokenPath(requestURI)){
            return JwtValidationResult.failure(Messages.Error.Auth.ACCESS_TOKEN_NOT_ALLOWED_ON_PATH + requestURI, HttpStatus.FORBIDDEN);
        }
        if(jwtType.equals(JwtType.ACCESS)){
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.INVALID_TOKEN_TYPE + jwtType, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public boolean supports(JwtValidationRequest jwtValidationRequest) {
        return JwtType.fromString(jwtValidationRequest.getJwt().getClaimAsString(JwtClaimsKey.TYPE)).equals(JwtType.ACCESS);
    }

    @Override
    public String getStrategyName() {
        return "accessTokenValidationStrategy";
    }
}
