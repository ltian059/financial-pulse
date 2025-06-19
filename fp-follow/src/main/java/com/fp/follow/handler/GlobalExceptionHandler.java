package com.fp.follow.handler;

import com.fp.common.exception.DuplicatedFollowException;
import com.fp.common.dto.auth.ExceptionResponseDTO;
import com.fp.common.exception.SelfFollowNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedFollowException.class)
    public ResponseEntity<ExceptionResponseDTO> handleDuplicatedFollowException(DuplicatedFollowException ex) {
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code("400")
                .message(ex.getMessage())
                .build();
        log.error(build.toString());
        return ResponseEntity.badRequest().body(build);
    }

    @ExceptionHandler(SelfFollowNotAllowedException.class)
    public ResponseEntity<ExceptionResponseDTO> handleSelfFollowNotAllowedException(SelfFollowNotAllowedException ex) {
        ExceptionResponseDTO build = ExceptionResponseDTO.builder()
                .code("400")
                .message(ex.getMessage())
                .build();
        log.error(build.toString());
        return ResponseEntity.badRequest().body(build);
    }
}
