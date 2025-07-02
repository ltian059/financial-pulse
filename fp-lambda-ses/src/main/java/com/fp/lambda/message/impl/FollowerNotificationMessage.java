package com.fp.lambda.message.impl;

import com.fp.lambda.enumeration.EmailType;
import com.fp.lambda.message.EmailMessage;
import lombok.*;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;

@Data
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

    @Override
    public EmailType getEmailType() {
        return EmailType.FOLLOWER_NOTIFICATION;
    }

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

}
