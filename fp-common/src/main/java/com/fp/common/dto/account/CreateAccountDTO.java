package com.fp.common.dto.account;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateAccountDTO implements Serializable {
    private String name;
    private String email;
    private String password;
}
