package com.fp.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fp.sqs.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void sendMessageBatch(String queueUrl, String messageGroupId, Message... messages) {
        List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
        for(int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            message.validate();
            try {
                String serializedBody = objectMapper.writeValueAsString(message.getMessageBody());
                SendMessageBatchRequestEntry.Builder entryBuilder = SendMessageBatchRequestEntry.builder();

                if (messageGroupId != null && !messageGroupId.trim().isEmpty()) {
                    entryBuilder.messageGroupId(messageGroupId);
                    // For FIFO queues, we can also set deduplication ID
                    entryBuilder.messageDeduplicationId(UUID.randomUUID().toString());
                }
                entryBuilder.messageBody(serializedBody)
                        .messageAttributes(message.getMessageAttributes())
                        .delaySeconds(message.getDelaySeconds())
                        .id(String.valueOf(i));

                entries.add(entryBuilder.build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if(!entries.isEmpty()){
            SendMessageBatchResponse response = sqsClient.sendMessageBatch(
                    SendMessageBatchRequest.builder()
                            .queueUrl(queueUrl)
                            .entries(entries)
                            .build()
            );

            log.info("Sent batch of {} messages to SQS queue: {}, {} successfull, {} failed",
                    entries.size(), queueUrl, response.successful().size(), response.failed().size());
        }

    }


    public void sendMessageBatch(String queueUrl, Message... messages) {
        sendMessageBatch(queueUrl, null, messages);
    }
}
