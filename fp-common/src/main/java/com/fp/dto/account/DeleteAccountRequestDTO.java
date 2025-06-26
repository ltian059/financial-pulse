package com.fp.dto.account;

import lombok.Data;

@Data
public class DeleteAccountRequestDTO {
    private String accountId;
    private String email;
}
