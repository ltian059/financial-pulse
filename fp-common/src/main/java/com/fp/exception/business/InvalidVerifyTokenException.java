package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidVerifyTokenException extends BusinessException {
    public InvalidVerifyTokenException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, cause);
    }

    public InvalidVerifyTokenException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }

    public InvalidVerifyTokenException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
