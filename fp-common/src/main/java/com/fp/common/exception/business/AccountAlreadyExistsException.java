package com.fp.common.exception.business;

import com.fp.common.exception.BusinessException;

public class AccountAlreadyExistsException extends BusinessException {
    public AccountAlreadyExistsException() {
        super("Account with the given email already exists: ");
    }

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
