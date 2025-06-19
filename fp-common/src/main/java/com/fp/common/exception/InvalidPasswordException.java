package com.fp.common.exception;

import com.fp.common.constant.MessageConstant;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ServiceException{
    public InvalidPasswordException(){
        super(HttpStatus.UNAUTHORIZED, MessageConstant.INVALID_CREDENTIALS);
    }
    public InvalidPasswordException(HttpStatus status, String responseBody) {
        super(status, responseBody);
    }

    public InvalidPasswordException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
