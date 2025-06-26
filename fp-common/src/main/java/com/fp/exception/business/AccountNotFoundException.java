package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND, Messages.Error.Account.NOT_FOUND);
    }

    public AccountNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }

}
