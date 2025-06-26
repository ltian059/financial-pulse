package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicatedFollowException extends BusinessException {
    public DuplicatedFollowException() {
        super(HttpStatus.CONFLICT, Messages.Error.Follow.FOLLOW_ALREADY_EXISTS);
    }
    public DuplicatedFollowException(String msg) {
        super(HttpStatus.CONFLICT, msg);
    }


}
