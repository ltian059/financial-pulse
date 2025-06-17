package com.fp.common.exception;

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
