package com.fp.exception;

/**
 * Custom base exception for the application.
 */
public class BaseException extends RuntimeException {
    public BaseException() {}
    public BaseException(String message) {
        super(message);
    }
}
