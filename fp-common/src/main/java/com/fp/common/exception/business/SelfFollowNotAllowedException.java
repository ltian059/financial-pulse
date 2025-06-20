package com.fp.common.exception.business;

import com.fp.common.constant.Messages;
import com.fp.common.exception.BusinessException;

public class SelfFollowNotAllowedException extends BusinessException {
    public SelfFollowNotAllowedException() {
        super(Messages.Error.Follow.SELF_FOLLOW_NOT_ALLOWED);
    }
    public SelfFollowNotAllowedException(String msg) {
        super(msg);
    }

}
