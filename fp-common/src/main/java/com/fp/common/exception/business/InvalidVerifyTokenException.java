package com.fp.common.exception.business;

import com.fp.common.exception.BusinessException;

public class InvalidVerifyTokenException extends BusinessException {
    public InvalidVerifyTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidVerifyTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVerifyTokenException(String message) {
        super(message);
    }
}
