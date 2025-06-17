package com.fp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponseDTO {
    private String code;
    private String message;
}
