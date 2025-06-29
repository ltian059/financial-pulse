package com.fp.auth.strategy;

import com.fp.util.UnauthorizedAuthClassifier;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class JwtValidationResult {
    private final boolean valid;

    private final String message;

    private final HttpStatusCode status;

    public static JwtValidationResult success(){
        return new JwtValidationResult(true, null, null);
    }
    public static JwtValidationResult failure(String message, HttpStatusCode code) {
        return new JwtValidationResult(false, message, code);
    }

    public JwtValidationResult(boolean valid, String message, HttpStatusCode status) {
        this.valid = valid;
        this.message = message;
        this.status = status;
    }
}
