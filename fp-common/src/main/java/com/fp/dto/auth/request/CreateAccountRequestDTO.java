package com.fp.dto.auth.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateAccountRequestDTO implements Serializable {
    private String name;
    private String email;
    private String password;
}
