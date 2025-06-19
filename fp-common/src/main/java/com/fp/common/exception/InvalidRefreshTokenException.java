package com.fp.common.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
public class InvalidRefreshTokenException extends ServiceException{
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

}
