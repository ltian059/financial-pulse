package com.fp.common.exception.business;

import com.fp.common.constant.Messages;
import com.fp.common.exception.BusinessException;

public class DuplicatedFollowException extends BusinessException {
    public DuplicatedFollowException() {
        super(Messages.Error.Follow.FOLLOW_ALREADY_EXISTS);
    }
    public DuplicatedFollowException(String msg) {
        super(msg);
    }


}
