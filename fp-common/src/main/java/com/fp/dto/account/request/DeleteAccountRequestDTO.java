package com.fp.dto.account.request;

import lombok.Data;

@Data
public class DeleteAccountRequestDTO {
    private String accountId;
    private String email;
}
