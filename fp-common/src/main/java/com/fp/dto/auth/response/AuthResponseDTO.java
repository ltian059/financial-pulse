package com.fp.dto.auth.response;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;

import static com.fp.util.UnauthorizedAuthClassifier.*;

/**
 * AuthResponseDTO is a Data Transfer Object used to encapsulate the response for authentication operations.
 */
@Data
@Builder
public class AuthResponseDTO {
    private int statusCode;
    private HttpStatusCode status;
    private String message;
    private String requestPath;
    private Instant timestamp;
    private UnauthorizedAuthInfo errorInfo;


    /**
     * Creates an AuthResponseDTO for unauthorized authentication.
     * @param requestPath
     * @param message
     * @return
     */
    public static AuthResponseDTO unauthorized(String requestPath, String message, UnauthorizedAuthInfo errorInfo) {
        return AuthResponseDTO.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .message(message != null ? message : HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .requestPath(requestPath)
                .timestamp(Instant.now())
                .errorInfo(errorInfo)
                .build();
    }

    public static AuthResponseDTO forbidden(String reqPath, String message) {
        return AuthResponseDTO.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN)
                .message(message != null ? message : HttpStatus.FORBIDDEN.getReasonPhrase())
                .requestPath(reqPath)
                .timestamp(Instant.now())
                .build();
    }
}
