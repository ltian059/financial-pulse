package com.fp.dto.follow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for SQS and SES messages related to follower notifications.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerNotificationDTO {
    //The account ID of the user making the request
    private String accountId;
    //The email of the user making the request
    private String email;
    private String followeeEmail;

    private String followerName;

    //The account ID of the user to be followed
    private String followeeId;
    private String followeeName;
}
