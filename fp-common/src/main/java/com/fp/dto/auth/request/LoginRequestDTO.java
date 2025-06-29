package com.fp.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
