package com.fp.common.util;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;

///
/// # Utility class to classify different types of unauthorized authentications
///
/// Authentication Errors have various causes, but when they occur, the status code is always 401.
///
/// In order to provide more specific error messages, we can classify these errors into different categories.
///
///
@RequiredArgsConstructor
@Slf4j
public class UnauthorizedAuthClassifier {

    @Getter
    public enum ErrorType {
        TOKEN_EXPIRED("TOKEN_EXPIRED", "Token has expired", true),
        TOKEN_MALFORMED("TOKEN_MALFORMED", "Token format is invalid", false),
        TOKEN_REVOKED("TOKEN_REVOKED", "Token has been revoked", false),
        INVALID_ISSUER("INVALID_ISSUER", "Token issuer is invalid", false),
        INVALID_AUDIENCE("INVALID_AUDIENCE", "Token audience is invalid", false),
        UNKNOWN_ERROR("UNKNOWN_ERROR", "Unknown authentication error", false),
        INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", "Token type is invalid", false);

        private final String type;
        private final String description;
        private final boolean canRefresh;
        ErrorType(String type, String description, boolean canRefresh) {
            this.type = type;
            this.description = description;
            this.canRefresh = canRefresh;
        }
    }


    /**
     * Classify the authentication exception and determine an error type
     * @param authException Parameter passed from custom authentication entry point
     * @param authHeader The request header containing the authentication token value.
     * @return Sealed class containing Auth error information
     */
    public static UnauthorizedAuthInfo classifyError(AuthenticationException authException, String authHeader) {
        //1. Check the auth header is missing
        if(authHeader == null || authHeader.trim().isEmpty()){
            return createErrorInfo(ErrorType.TOKEN_MALFORMED, true);
        }
        //2. Check if the token format is correct
        if(!authHeader.startsWith("Bearer ")){
            return createErrorInfo(ErrorType.TOKEN_MALFORMED, true);
        }
        //3. Further analyze the token.
        String token = authHeader.substring(7);
        if(token.trim().isEmpty()){
            return  createErrorInfo(ErrorType.TOKEN_MALFORMED, true);
        }

        return analyzeException(authException);
    }

    protected static UnauthorizedAuthInfo analyzeException(AuthenticationException authException) {
        String message = authException.getMessage();
        if(message==null || message.trim().isEmpty()){
            message = "";
        }
        if(message.contains("Jwt expired at")){
            return createErrorInfo(ErrorType.TOKEN_EXPIRED, false);
        }

        return createErrorInfo(ErrorType.TOKEN_MALFORMED, true);
    }

    public static UnauthorizedAuthInfo createErrorInfo(ErrorType errorType, boolean requiresLogin) {
        return UnauthorizedAuthInfo.builder()
                .errorType(errorType.getType())
                .errorDescription(errorType.getDescription())
                .canRefresh(errorType.isCanRefresh())
                .requiresLogin(requiresLogin)
                .build();
    }
    @Builder
    @Data
    public static class UnauthorizedAuthInfo {
        private String errorType;
        private String errorDescription;
        private boolean canRefresh;
        private boolean requiresLogin;
    }

}
