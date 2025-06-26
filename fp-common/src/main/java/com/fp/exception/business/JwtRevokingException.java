package com.fp.exception.business;

import com.fp.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JwtRevokingException extends BusinessException {
    public JwtRevokingException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR,"JWT revoking failed");
    }

    public JwtRevokingException(String msg) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    public JwtRevokingException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "JWT revoking failed", cause);
    }
}
