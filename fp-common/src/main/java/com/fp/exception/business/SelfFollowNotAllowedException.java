package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;

public class SelfFollowNotAllowedException extends BusinessException {
    public SelfFollowNotAllowedException() {
        super(Messages.Error.Follow.SELF_FOLLOW_NOT_ALLOWED);
    }
    public SelfFollowNotAllowedException(String msg) {
        super(msg);
    }

}
