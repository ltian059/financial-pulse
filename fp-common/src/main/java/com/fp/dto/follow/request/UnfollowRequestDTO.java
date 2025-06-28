package com.fp.dto.follow.request;

import lombok.Data;

@Data
public class UnfollowRequestDTO {
    /**
     * The ID of the account that is unfollowing another account.
     */
    private String accountId;
    /**
     * The email of the account that is unfollowing another account.
     */
    private String email;
    /**
     * The ID of the account that is being unfollowed.
     */
    private String followeeId;
}
