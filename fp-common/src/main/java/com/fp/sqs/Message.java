package com.fp.sqs;

import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;

/**
 * All message sent to SQS should implement this interface.
 */
public interface Message {
    Map<String, Object> getMessageBody();

    /**
     * Get the message attributes.
     */
    Map<String, MessageAttributeValue> getMessageAttributes();


    MessageType getMessageType();

    default Integer getPriority() {
        return 5;
    }

    default Integer getDelaySeconds() {
        return 0;
    }

    default Integer getRetryCount() {
        return 0;
    }

    default void validate() {
        if(getMessageBody() == null){
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }
        if(getMessageType() == null){
            throw new IllegalArgumentException("Message type cannot be null");
        }
    }

}
