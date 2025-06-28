package com.fp.dto.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerifyRequestDTO {
    private String accountId;
    private String email;
}
