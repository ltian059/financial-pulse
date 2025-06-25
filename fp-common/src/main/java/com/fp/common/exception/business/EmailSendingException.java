package com.fp.common.exception.business;

import com.fp.common.exception.BusinessException;
import com.fp.common.exception.ServiceException;
import org.springframework.http.HttpStatus;

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
