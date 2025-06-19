package com.fp.account.handler;

import com.fp.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String > handleServiceException(ServiceException ex) {
        log.error(ex.getResponseBody());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBody());
    }
}
