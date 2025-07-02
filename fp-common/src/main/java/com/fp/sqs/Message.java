package com.fp.sqs;

import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;

/**
 * All message sent to SQS should implement this interface.
 */
public interface Message {

    /**
     * Get the message attributes.
     */
    Map<String, MessageAttributeValue> getMessageAttributes();
    MessageType getMessageType();
    Map<String, Object> getMessageBody();
    String getSource();


    Integer getPriority();
    Integer getDelaySeconds();
    Integer getRetryCount();

    void validate();


}
