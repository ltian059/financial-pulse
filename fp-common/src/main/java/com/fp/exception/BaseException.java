package com.fp.exception;

///  # Base exception for all custom exceptions in the application.
///
///  Provides a foundation for creating domain-specific exceptions.
///
public abstract class BaseException extends RuntimeException {
    protected BaseException() {
        super();
    }

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseException(Throwable cause) {
        super(cause);
    }

}
