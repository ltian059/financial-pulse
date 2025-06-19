package com.fp.common.dto.auth;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * AuthResponseDTO is a Data Transfer Object used to encapsulate the response for authentication operations.
 */
@Data
@Builder
public class AuthResponseDTO {
    private String code;
    private String message;
    private String requestPath;
    private Instant timestamp;

    /**
     * Creates an AuthResponseDTO for unauthorized authentication.
     * @param reqPath
     * @param message
     * @return
     */
    public static AuthResponseDTO unauthorized(String reqPath, String message) {
        return AuthResponseDTO.builder()
                .code("401")
                .message(message != null ? message : "Unauthorized Access.")
                .requestPath(reqPath)
                .timestamp(Instant.now())
                .build();
    }

    public static AuthResponseDTO forbidden(String reqPath, String message) {
        return AuthResponseDTO.builder()
                .code("403")
                .message(message != null ? message : "Forbidden Access.")
                .requestPath(reqPath)
                .timestamp(Instant.now())
                .build();
    }
}
