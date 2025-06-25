package com.fp.exception.business;

import com.fp.exception.BusinessException;

public class AccountAlreadyExistsException extends BusinessException {
    public AccountAlreadyExistsException() {
        super("Account with the given email already exists: ");
    }

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
