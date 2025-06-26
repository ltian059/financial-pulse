package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidJwtTypeException extends BusinessException {
    public InvalidJwtTypeException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
