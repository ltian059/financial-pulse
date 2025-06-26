package com.fp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

///
/// Exception representing service-level errors with HTTP status information.
///
/// This exception is designed for REST API error responses.
@Getter
public abstract class ServiceException extends BaseException{
    private final HttpStatus httpStatus;

    protected ServiceException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public int getStatusCode() {
        return httpStatus.value();
    }

}
