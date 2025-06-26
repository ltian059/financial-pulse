package com.fp.exception.business;

import com.fp.constant.Messages;
import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SelfFollowNotAllowedException extends BusinessException {
    public SelfFollowNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, Messages.Error.Follow.SELF_FOLLOW_NOT_ALLOWED);
    }
    public SelfFollowNotAllowedException(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }

}
