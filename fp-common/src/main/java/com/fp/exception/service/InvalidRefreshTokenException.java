package com.fp.exception.service;


import com.fp.constant.Messages;
import com.fp.exception.ServiceException;
import org.springframework.http.HttpStatus;


public class InvalidRefreshTokenException extends ServiceException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, Messages.Error.Auth.INVALID_REFRESH_TOKEN);
    }

}
