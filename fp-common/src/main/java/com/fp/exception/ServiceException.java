package com.fp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

///
/// Exception representing service-level errors with HTTP status information.
///
/// This exception is designed for REST API error responses.
@Getter
public class ServiceException extends BaseException{
    private final int statusCode;
    private final HttpStatus status;
    private final String responseBody;

    public ServiceException(int statusCode, String responseBody) {
        super(responseBody);
        this.statusCode = statusCode;
        this.status = HttpStatus.valueOf(statusCode);
        this.responseBody = responseBody;
    }
    public ServiceException(HttpStatus status, String responseBody) {
        super(responseBody);
        this.status = status;
        this.statusCode = status.value();
        this.responseBody = responseBody;
    }

}
