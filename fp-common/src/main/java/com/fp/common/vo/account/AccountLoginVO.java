package com.fp.common.vo.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountLoginVO {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthday;

    private String accessToken;
    private String refreshToken;

}
