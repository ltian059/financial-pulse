package com.fp.service.impl;

import com.fp.sqs.EmailMessage;
import com.fp.properties.SqsProperties;
import com.fp.sqs.Message;
import com.fp.sqs.service.AbstractSqsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
@Slf4j
public class AccountSqsServiceImpl extends AbstractSqsService {

    private final SqsProperties sqsProperties;

    public AccountSqsServiceImpl(SqsClient sqsClient, SqsProperties sqsProperties) {
        super(sqsClient);
        this.sqsProperties = sqsProperties;
    }


    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        //Try to set metadata it not already set
        try {
            sendMessage(sqsProperties.getEmailQueue().getQueueUrl(), emailMessage);
            log.info("Email message sent to SQS:, body={}, type={}",
                    emailMessage.getMessageBody(),
                    emailMessage.getMessageType());
        }catch (Exception e){
            log.error("Failed to send email message to SQS: {}", emailMessage, e);
            throw new RuntimeException("Failed to send email message to SQS", e);
        }
    }


}
