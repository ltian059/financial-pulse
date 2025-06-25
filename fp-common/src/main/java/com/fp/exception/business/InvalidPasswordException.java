package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(){
        super(Messages.Error.Account.INVALID_PASSWORD);
    }
    public InvalidPasswordException(String msg) {
        super(msg);
    }

}
