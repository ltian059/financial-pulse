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

import static com.fp.util.HttpUtil.isVerificationTokenPath;

@StrategyComponent(
        value = "verificationTokenValidationStrategy",
        description = "Strategy to validate verification tokens",
        priority = 1
)
public class VerificationTokenValidationStrategy extends AbstractJwtValidationStrategy {
    @Override
    protected JwtValidationResult validateJwt(JwtValidationRequest jwtValidationRequest) {
        Jwt jwt = jwtValidationRequest.getJwt();
        JwtType type = JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE));
        String requestUri = jwtValidationRequest.getRequestURI();
        if(JwtType.VERIFICATION.equals(type) && isVerificationTokenPath(requestUri)) {
            return JwtValidationResult.success();
        }
        return JwtValidationResult.failure(Messages.Error.Auth.VERIFICATION_TOKEN_NOT_ALLOWED_ON_PATH + requestUri, HttpStatus.FORBIDDEN);

    }

    @Override
    public boolean supports(JwtValidationRequest type) {
        Jwt jwt = type.getJwt();
        JwtType jwtType = JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE));
        return jwtType.equals(JwtType.VERIFICATION);
    }

    @Override
    public String getStrategyName() {
        return "verificationTokenValidationStrategy";
    }

}
