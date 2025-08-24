package com.fp.sqs.service.impl;

import com.fp.properties.SqsProperties;
import com.fp.sqs.email.EmailMessage;
import com.fp.sqs.service.AbstractSqsService;
import com.fp.sqs.service.EmailSqsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
@ConditionalOnBean(SqsClient.class)
public class EmailSqsServiceImpl extends AbstractSqsService implements EmailSqsService {
    private final SqsProperties sqsProperties;

    public EmailSqsServiceImpl(SqsClient sqsClient, SqsProperties sqsProperties) {
        super(sqsClient);
        this.sqsProperties = sqsProperties;
    }


    @Override
    public void sendEmailMessage(EmailMessage message, String queueUrl) {
        super.sendMessage(queueUrl, message);
    }

    @Override
    public void sendEmailMessage(EmailMessage message) {
        sendEmailMessage(message, sqsProperties.getEmailQueue().getQueueUrl());
    }

    public void sendEmailMessageBatch(EmailMessage ... messages) {
        String queueUrl = sqsProperties.getEmailQueue().getQueueUrl();
        super.sendMessageBatch(queueUrl, null, messages);
    }
}
