package com.fp.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public class ServiceException extends BaseException{
    private final int statusCode;
    private final String responseBody;

    public ServiceException(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
