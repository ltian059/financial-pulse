package com.fp.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

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
    private Instant createdAt;
}
