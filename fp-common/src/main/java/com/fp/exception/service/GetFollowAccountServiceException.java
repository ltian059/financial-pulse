package com.fp.exception.service;

import com.fp.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class GetFollowAccountServiceException extends ServiceException {
    public GetFollowAccountServiceException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Problems occurred while getting follow account information.");
    }

    public GetFollowAccountServiceException(HttpStatus status, String message) {
        super(status, message);
    }
    public GetFollowAccountServiceException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
    public GetFollowAccountServiceException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
