package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class BirthdayFormatParseException extends BusinessException {
    public BirthdayFormatParseException() {
        super(HttpStatus.BAD_REQUEST, Messages.Error.Account.BIRTHDAY_FORMAT_ERROR);
    }
    public BirthdayFormatParseException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
    public BirthdayFormatParseException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
    public BirthdayFormatParseException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, Messages.Error.Account.BIRTHDAY_FORMAT_ERROR, cause);
    }
}
