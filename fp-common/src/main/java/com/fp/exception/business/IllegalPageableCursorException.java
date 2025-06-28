package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class IllegalPageableCursorException extends BusinessException {
    public IllegalPageableCursorException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public IllegalPageableCursorException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }

    public IllegalPageableCursorException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, cause);
    }
}
