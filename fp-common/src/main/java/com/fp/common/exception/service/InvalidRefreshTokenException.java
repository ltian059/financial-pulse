package com.fp.common.exception.service;


import com.fp.common.constant.Messages;
import com.fp.common.exception.ServiceException;
import org.springframework.http.HttpStatus;


public class InvalidRefreshTokenException extends ServiceException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, Messages.Error.Auth.INVALID_REFRESH_TOKEN);
    }

}
