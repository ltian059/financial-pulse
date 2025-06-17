package com.fp.common.vo.account;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateAccountVO implements Serializable {
    private String name;
    private String email;
    private String password;
}
