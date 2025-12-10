package com.fp.exception.handler;

import com.fp.dto.common.ExceptionResponseDTO;
import com.fp.exception.BusinessException;
import com.fp.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDTO> handle(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        for(var error : ex.getBindingResult().getAllErrors()){
            errorMessage.append(error.getDefaultMessage()).append(";");
        }
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage.toString())
                .build();
        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponseDTO> handle(MethodArgumentTypeMismatchException ex){
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        log.error("Method argument type mismatch: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponseDTO> handleGenericException(Exception ex) {
//        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .build();
//        log.error("Unexpected error: ", ex);
//        return ResponseEntity.status(500).body(build);
//    }
}
