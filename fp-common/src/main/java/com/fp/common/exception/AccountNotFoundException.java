package com.fp.common.exception;

import com.fp.common.constant.MessageConstant;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends ServiceException {
    public AccountNotFoundException() {
        super(HttpStatus.BAD_REQUEST, MessageConstant.ACCOUNT_NOT_FOUND);
    }

    public AccountNotFoundException(HttpStatus status, String responseBody) {
        super(status, responseBody);
    }

    public AccountNotFoundException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
