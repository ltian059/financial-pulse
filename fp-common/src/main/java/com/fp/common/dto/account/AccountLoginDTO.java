package com.fp.common.dto.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountLoginDTO {
    private String email;
    private String password;

}
