package com.fp.exception;

/// # BusinessException
///   Exception for business logic violations.
///
///  Use this for domain-specific business rule violations that don't require HTTP status codes.
///
public class BusinessException extends BaseException {
    protected BusinessException() {
    }

    protected BusinessException(String message) {
        super(message);
    }

    protected  BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BusinessException(Throwable cause) {
        super(cause);
    }

}
