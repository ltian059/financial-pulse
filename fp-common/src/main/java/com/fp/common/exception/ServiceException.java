package com.fp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends BaseException{
    private final int statusCode;
    private final HttpStatus status;
    private final String responseBody;

    public ServiceException(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.status = HttpStatus.valueOf(statusCode);
        this.responseBody = responseBody;
    }
    public ServiceException(HttpStatus status, String responseBody) {
        this.status = status;
        this.statusCode = status.value();
        this.responseBody = responseBody;
    }

}
