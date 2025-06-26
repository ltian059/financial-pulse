package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JwtRevokedException extends BusinessException {
    public JwtRevokedException() {
        super(HttpStatus.UNAUTHORIZED, "JWT token has been revoked");
    }

    public JwtRevokedException(String msg) {
        super(HttpStatus.UNAUTHORIZED, msg);
    }
}
