package com.fp.common.exception.business;

import com.fp.common.constant.Messages;
import com.fp.common.exception.BusinessException;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(Messages.Error.Account.NOT_FOUND_BY_EMAIL);
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
