package com.fp.dto.account;

import lombok.Data;

@Data
public class FollowAccountRequestDTO {
    //The account ID of the user making the request
    private String accountId;
    //The email of the user making the request
    private String email;
    //The account ID of the user to be followed
    private String followeeId;
}
