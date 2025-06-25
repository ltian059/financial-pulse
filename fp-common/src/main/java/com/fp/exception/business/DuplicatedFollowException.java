package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;

public class DuplicatedFollowException extends BusinessException {
    public DuplicatedFollowException() {
        super(Messages.Error.Follow.FOLLOW_ALREADY_EXISTS);
    }
    public DuplicatedFollowException(String msg) {
        super(msg);
    }


}
