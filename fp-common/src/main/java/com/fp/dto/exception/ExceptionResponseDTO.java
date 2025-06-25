package com.fp.dto.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponseDTO {
    private int code;
    private String message;
}
