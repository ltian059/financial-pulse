package com.fp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

///
/// Exception representing service-level errors with HTTP status information.
///
/// This exception is designed for REST API error responses.
@Getter
public abstract class ServiceException extends BaseException{
    private final HttpStatusCode httpStatus;

    protected ServiceException(HttpStatusCode httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public int getStatusCode() {
        return httpStatus.value();
    }

    protected ServiceException(HttpStatusCode httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
    protected ServiceException(HttpStatusCode httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }

}
