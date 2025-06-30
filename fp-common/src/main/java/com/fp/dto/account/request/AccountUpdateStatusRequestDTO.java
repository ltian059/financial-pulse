package com.fp.dto.account.request;

import lombok.Data;

@Data
public class AccountUpdateStatusRequestDTO {
    private String accountId;
    private String email;
    private Boolean verified;
}
