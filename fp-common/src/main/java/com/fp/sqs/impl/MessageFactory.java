package com.fp.sqs.impl;

import org.springframework.stereotype.Component;

public class MessageFactory {
    public static VerificationEmailMessage createVerificationEmailMessage(
            String verificationToken,
            String accountId,
            String email,
            String name,
            String source) {
        return VerificationEmailMessage.builder()
                .verificationToken(verificationToken)
                .accountId(accountId)
                .email(email)
                .name(name)
                .source(source)
                .build();
    }


    public static FollowerNotificationMessage createFollowerNotificationMessage(
            String followerName,
            String followeeId,
            String followeeName,
            String followeeEmail,
            String source) {
        return FollowerNotificationMessage.builder()
                .followerName(followerName)
                .followeeId(followeeId)
                .followeeName(followeeName)
                .followeeEmail(followeeEmail)
                .source(source)
                .build();
    }

}
