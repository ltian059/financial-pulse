package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class BirthdayInFutureException extends BusinessException {
    public BirthdayInFutureException() {
        super(HttpStatus.BAD_REQUEST, Messages.Error.Account.BIRTHDAY_FORMAT_ERROR);
    }
}
