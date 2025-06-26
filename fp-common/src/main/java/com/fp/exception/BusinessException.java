package com.fp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/// # BusinessException
///   Exception for business logic violations.
///
///  Use this for domain-specific business rule violations that don't require HTTP status codes.
///
@Getter
public abstract class BusinessException extends BaseException {
    private final HttpStatus httpStatus;

    protected BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatus httpStatus, int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
    public int getStatusCode(){
        return httpStatus.value();
    }
}
