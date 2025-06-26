package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountAlreadyVerifiedException extends BusinessException {
    public  AccountAlreadyVerifiedException(String msg) {
        super(HttpStatus.CONFLICT, msg);
    }
    public AccountAlreadyVerifiedException() {
        super(HttpStatus.CONFLICT, Messages.Error.Account.ALREADY_VERIFIED);
    }
}
