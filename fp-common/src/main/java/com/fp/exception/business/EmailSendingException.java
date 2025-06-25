package com.fp.exception.business;

import com.fp.exception.BusinessException;

public class EmailSendingException extends BusinessException {
    public EmailSendingException(String message) {
        super(message);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSendingException(Throwable cause) {
        super(cause);
    }
}
