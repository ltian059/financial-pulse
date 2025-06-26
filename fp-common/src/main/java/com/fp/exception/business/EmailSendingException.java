package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailSendingException extends BusinessException {
    public EmailSendingException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.Account.EMAIL_SENDING_ERROR);
    }

    public EmailSendingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
