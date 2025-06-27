package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JwtRepositoryNotFoundException extends BusinessException {
    public JwtRepositoryNotFoundException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR,"JWT repository not found. Please ensure that the JWT repository is properly configured.");
    }

    public JwtRepositoryNotFoundException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public JwtRepositoryNotFoundException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

    public JwtRepositoryNotFoundException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
