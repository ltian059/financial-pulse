package com.fp.common.vo.account;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountVO {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthday;

    private Boolean verified;
}
