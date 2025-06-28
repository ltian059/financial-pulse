package com.fp.handler;

import com.fp.dto.common.ExceptionResponseDTO;
import com.fp.exception.BusinessException;
import com.fp.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
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
        return ResponseEntity.status(ex.getHttpStatus()).body(build);
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBusinessException(BusinessException ex) {
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code(ex.getStatusCode())
                .message(ex.getMessage())
                .build();
        log.error(build.toString());
        return ResponseEntity.status(ex.getHttpStatus()).body(build);
    }
}
