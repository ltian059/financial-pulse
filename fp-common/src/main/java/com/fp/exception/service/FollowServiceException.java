package com.fp.exception.service;

import com.fp.exception.ServiceException;
import org.springframework.http.HttpStatusCode;

public class FollowServiceException extends ServiceException {
    public FollowServiceException(HttpStatusCode status, String message) {
        super(status, message);
    }

    public FollowServiceException(HttpStatusCode status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
