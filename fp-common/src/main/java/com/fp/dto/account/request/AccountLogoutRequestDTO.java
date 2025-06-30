package com.fp.dto.account.request;

import lombok.Data;

@Data
public class AccountLogoutRequestDTO {
    private String email;
    private String accountId;
}
