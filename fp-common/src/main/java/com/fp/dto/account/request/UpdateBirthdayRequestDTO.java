package com.fp.dto.account.request;

import lombok.Data;

@Data
public class UpdateBirthdayRequestDTO {
    String accountId;
    String email;

    String birthday; // Format: MM-dd-yyyy
}
