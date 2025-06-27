package com.fp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/// # BusinessException
///   Exception for business logic violations.
///
///  Use this for domain-specific business rule violations that don't require HTTP status codes.
///
@Getter
public abstract class BusinessException extends BaseException {
    private final HttpStatusCode httpStatus;

    protected BusinessException(HttpStatusCode httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatusCode httpStatus, int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatusCode httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
    protected BusinessException(HttpStatusCode httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
    public int getStatusCode(){
        return httpStatus.value();
    }
}
