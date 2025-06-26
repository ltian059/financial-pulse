package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountAlreadyExistsException extends BusinessException {
    public AccountAlreadyExistsException() {
        super(HttpStatus.CONFLICT, Messages.Error.Account.ALREADY_EXISTS);
    }
    public AccountAlreadyExistsException(String msg) {
        super(HttpStatus.CONFLICT, msg);
    }
}
