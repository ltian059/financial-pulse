package com.fp.sqs.service;

import com.fp.sqs.email.EmailMessage;
import com.fp.sqs.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * SQS service interface for sending messages to queues.
 */
public interface SqsService {


    /**
     * Send a generic message to specified queue (publish)
     * @param queueUrl the target queue URL
     * @param message the message object
     * @param messageGroupId optional message group ID for FIFO queues
     */
    void sendMessage(String queueUrl, Message message, String messageGroupId);

    /**
     * Send a generic message to specified queue (standard queue)
     * @param queueUrl the target queue URL
     * @param message the message object
     */
    void sendMessage(String queueUrl, Message message);

    void sendMessageBatch(String queueUrl, String messageGroupId, Message... messages);

}
