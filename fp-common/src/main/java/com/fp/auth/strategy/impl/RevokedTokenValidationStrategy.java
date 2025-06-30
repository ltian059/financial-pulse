package com.fp.auth.strategy.impl;

import com.fp.auth.service.RevokedJwtValidationService;
import com.fp.auth.strategy.AbstractJwtValidationStrategy;
import com.fp.auth.strategy.JwtValidationRequest;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.constant.Messages;
import com.fp.pattern.annotation.StrategyComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

@StrategyComponent(
        value = "revokedTokenValidationStrategy",
        description = "Strategy to validate revoked tokens",
        priority = 0 //higher priority for revoked token checks
)
@RequiredArgsConstructor
public class RevokedTokenValidationStrategy extends AbstractJwtValidationStrategy {

    private final RevokedJwtValidationService revokedJwtValidationService;

    @Override
    protected JwtValidationResult validateJwt(JwtValidationRequest jwtValidationRequest) {
        return revokedJwtValidationService.isTokenRevoked(jwtValidationRequest.getJwt())
                ? JwtValidationResult.failure(Messages.Error.Auth.JWT_REVOKED, HttpStatus.UNAUTHORIZED)
                : JwtValidationResult.success();
    }

    @Override
    public boolean supports(JwtValidationRequest request) {
        Jwt jwt  = request.getJwt();
        String requestURI = request.getRequestURI();
        return jwt != null && requestURI != null && jwt.getId() != null;
    }

    @Override
    public String getStrategyName() {
        return "revokedTokenValidationStrategy";
    }

    @Override
    public int getPriority() {
        return 0; // Higher priority for revoked token checks
    }
}
