package com.fp.sqs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fp.sqs.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSqsService implements SqsService {
    private final SqsClient sqsClient;
    private final static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void sendMessage(String queueUrl, Message message, String messageGroupId){
        try {
            message.validate();

            String serializedBody = objectMapper.writeValueAsString(message.getMessageBody());

            SendMessageRequest.Builder requestBuilder = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(serializedBody)
                    .messageAttributes(message.getMessageAttributes())
                    .delaySeconds(message.getDelaySeconds());

            // Add message group ID for FIFO queues
            if (messageGroupId != null && !messageGroupId.trim().isEmpty()) {
                requestBuilder.messageGroupId(messageGroupId);
                // For FIFO queues, we can also set deduplication ID
                requestBuilder.messageDeduplicationId(UUID.randomUUID().toString());
            }

            SendMessageResponse response = sqsClient.sendMessage(requestBuilder.build());

            log.debug("Message sent to SQS successfully: messageId={}, queueUrl={}",
                    response.messageId(), queueUrl);

        } catch (SqsException e) {
            log.error("SQS error when sending message to queue {}: {}", queueUrl, e.getMessage(), e);
            throw new RuntimeException("SQS error when sending message", e);
        } catch (Exception e) {
            log.error("Unexpected error when sending message to queue {}: {}", queueUrl, e.getMessage(), e);
            throw new RuntimeException("Unexpected error when sending message", e);
        }
    }

    @Override
    public void sendMessage(String queueUrl, Message message) {
        sendMessage(queueUrl, message, null);
    }
}
