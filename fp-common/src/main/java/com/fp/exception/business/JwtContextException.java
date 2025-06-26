package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JwtContextException extends BusinessException {
    public JwtContextException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public JwtContextException(int statusCode, String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, statusCode, message, cause);
    }

    public JwtContextException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause);
    }

    public JwtContextException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }
}
