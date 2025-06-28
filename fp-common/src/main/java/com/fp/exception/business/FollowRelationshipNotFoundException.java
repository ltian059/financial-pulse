package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FollowRelationshipNotFoundException extends BusinessException{
    public FollowRelationshipNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Follow relationship not found");
    }

    public FollowRelationshipNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public FollowRelationshipNotFoundException(String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND, message, cause);
    }

    public FollowRelationshipNotFoundException(Throwable cause) {
        super(HttpStatus.NOT_FOUND, cause);
    }
}
