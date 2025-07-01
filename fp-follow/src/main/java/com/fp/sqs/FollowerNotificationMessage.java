package com.fp.sqs;

import lombok.*;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerNotificationMessage implements EmailMessage {
    private String followerId;
    private String followerName;
    private String followerEmail;

    private String followeeId;
    private String followeeName;
    private String followeeEmail;

    private String source;
    @Override
    public EmailType getEmailType() {
        return EmailType.FOLLOWER_NOTIFICATION;
    }

    /**
     * Get the account ID for the followee because the follower notification is sent to the followee.
     * @return
     */
    @Override
    public String getAccountId() {
        return followeeId;
    }

    @Override
    public String getEmail() {
        return followeeEmail;
    }

    @Override
    public String getName() {
        return followeeName;
    }

    @Override
    public String getSource() {
        return source;
    }


    @Override
    public Map<String, Object> getMessageBody() {
        return Map.of(
                "followerId", followerId,
                "followerName", followerName,
                "followerEmail", followerEmail,
                "followeeId", followeeId,
                "followeeName", followeeName,
                "followeeEmail", followeeEmail
        );
    }

}
