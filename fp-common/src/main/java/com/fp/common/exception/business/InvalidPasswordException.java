package com.fp.common.exception.business;

import com.fp.common.constant.Messages;
import com.fp.common.exception.BusinessException;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(){
        super(Messages.Error.Account.INVALID_PASSWORD);
    }
    public InvalidPasswordException(String msg) {
        super(msg);
    }

}
