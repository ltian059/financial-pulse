package com.fp.auth.strategy;

import com.fp.constant.JwtClaimsKey;
import com.fp.constant.Messages;
import com.fp.enumeration.jwt.JwtType;
import com.fp.exception.business.InvalidJwtTypeException;
import com.fp.repository.RevokedJwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

@RequiredArgsConstructor
public abstract class AbstractJwtValidationStrategy implements JwtValidationStrategy {

    protected final RevokedJwtRepository revokedJwtRepository;

    protected abstract boolean supportsJwtType(JwtType jwtType);

    protected abstract JwtValidationResult validateJwtType(JwtType jwtType, String requestUri);



    @Override
    public JwtValidationResult postValidateJwt(Jwt jwt, String requestURI) {
        //1. validate JWT type against the request URI
        var typeValidationResult = validateJwtType(
                JwtType.fromString(jwt.getClaimAsString(JwtClaimsKey.TYPE)),
                requestURI
        );
        if(!typeValidationResult.isValid()){
            return typeValidationResult;
        }

        //2. Validate if the JWT type is revoked.
        var isRevokedResult = validateJwtIsRevoked(jwt);
        if(!isRevokedResult.isValid()){
            return isRevokedResult;
        }

        //3. If the JWT is valid and not revoked, return success.

        return JwtValidationResult.success();

    }

    protected JwtValidationResult validateJwtIsRevoked(Jwt jwt){
        if(revokedJwtRepository.exists(jwt)){
            return JwtValidationResult.failure(Messages.Error.Auth.JWT_REVOKED, HttpStatus.UNAUTHORIZED);
        }
        return JwtValidationResult.success();
    }
    @Override
    public boolean supports(JwtValidationRequest request) {
        try {
            JwtType type = JwtType.fromString(request.getJwt().getClaimAsString(JwtClaimsKey.TYPE));
            return supportsJwtType(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtTypeException(Messages.Error.Auth.INVALID_TOKEN_TYPE, e);
        }
    }
}
