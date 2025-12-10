package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatusCode;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException(HttpStatusCode httpStatus, String message) {
        super(httpStatus, message);
    }
}
