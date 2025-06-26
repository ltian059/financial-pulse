package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(){
        super(HttpStatus.BAD_REQUEST, Messages.Error.Account.INVALID_PASSWORD);
    }
    public InvalidPasswordException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }

}
