package com.fp.service.impl;

import com.fp.properties.SqsProperties;
import com.fp.sqs.Message;
import com.fp.sqs.service.AbstractSqsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
@Slf4j
public class FollowSqsServiceImpl extends AbstractSqsService {
    private final SqsProperties sqsProperties;
    public FollowSqsServiceImpl(SqsClient sqsClient, SqsProperties sqsProperties) {
        super(sqsClient);
        this.sqsProperties = sqsProperties;
    }

    @Override
    public void sendFollowerNotificationMessage(Message followerNotificationMessage) {
        try {
            // Set metadata if not already set
            sendMessage(sqsProperties.getFollowerNotificationQueue().getQueueUrl(), followerNotificationMessage);
            log.info("Follow notification message sent to SQS: body ={}, type={}",
                    followerNotificationMessage.getMessageBody(),
                    followerNotificationMessage.getMessageType()
            );

        } catch (Exception e) {
            log.error("Failed to send follow notification message to SQS: {}", followerNotificationMessage, e);
            throw new RuntimeException("Failed to send follow notification message to SQS", e);
        }
    }
}
