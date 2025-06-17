package com.fp.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean verified;
    private LocalDate birthday;
    private LocalDateTime createdAt;
}
