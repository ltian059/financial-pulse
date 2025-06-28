package com.fp.dto.follow.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowPaginationRequestDTO {
    @NotBlank
    private String accountId;

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    @Builder.Default
    private Integer limit = 20;

    private String cursor;

    @Pattern(regexp = "asc|desc", message = "Order must be 'asc' or 'desc'")
    @Builder.Default
    private String order = "desc";

    @NotNull
    private QueryType queryType;

    public enum QueryType {
        // query for followers of an account
        FOLLOWERS,
        // query for accounts that the user is following
        FOLLOWINGS
    }
}
