package com.fp.sqs.impl;

import com.fp.sqs.email.AbstractEmailMessage;
import com.fp.sqs.email.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerNotificationMessage extends AbstractEmailMessage {
    private String followerName;

    private String followeeId;
    private String followeeName;
    private String followeeEmail;

    private String source;
    @Override
    public EmailType getEmailType() {
        return EmailType.FOLLOWER_NOTIFICATION;
    }

    @Override
    protected void validateEmailSpecific() {
        if(followeeId == null || followeeId.isEmpty()) {
            throw new IllegalArgumentException("Followee ID cannot be null or empty");
        }
        if(followeeEmail == null || followeeEmail.isEmpty()) {
            throw new IllegalArgumentException("Followee Email cannot be null or empty");
        }
        if(followeeName == null || followeeName.isEmpty()) {
            throw new IllegalArgumentException("Followee Name cannot be null or empty");
        }
        if(followerName == null || followerName.isEmpty()) {
            throw new IllegalArgumentException("Follower Name cannot be null or empty");
        }
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

    public Map<String, Object> getMessageBodyAsMap() {
        return Map.of();
    }

    public Map<String, Object> getMessageBody() {
        return Map.of(
                "followerName", followerName,
                "followeeId", followeeId,
                "followeeName", followeeName,
                "followeeEmail", followeeEmail
        );
    }

}
