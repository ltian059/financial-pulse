package com.fp.dto.follow.request;

import lombok.Data;

@Data
public class FollowRequestDTO {
    //The account ID of the user making the request
    private String accountId;
    //The email of the user making the request
    private String email;

    private String followerName;

    //The account ID of the user to be followed
    private String followeeId;
    private String followeeEmail;
    private String followeeName;
}
