package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(Messages.Error.Account.NOT_FOUND_BY_EMAIL);
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
