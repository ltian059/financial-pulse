package com.fp.account.handler;

import com.fp.common.dto.exception.ExceptionResponseDTO;
import com.fp.common.exception.BusinessException;
import com.fp.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ExceptionResponseDTO> handleServiceException(ServiceException ex) {
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code(ex.getStatusCode())
                .message(ex.getMessage())
                .build();
        log.error(build.toString());
        return ResponseEntity.status(ex.getStatus()).body(build);
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBusinessException(BusinessException ex) {
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        log.error(build.toString());
        return ResponseEntity.internalServerError().body(build);
    }
}
