package com.fp.dto.account.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDTO {
    private String accountId;

    private String name;

    private String email;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate birthday;

    private Instant createdAt;

    private Instant modifiedAt;

    private String labels;

    private Boolean verified;
}
