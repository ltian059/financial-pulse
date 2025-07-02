package com.fp.sqs;

import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMessage implements Message{
    @Override
    public Integer getPriority() {
        return 5;
    }

    @Override
    public Integer getDelaySeconds() {
        return 0;
    }

    @Override
    public Integer getRetryCount() {
        return 0;
    }

    protected abstract void validateSpecific();
    /**
     * Common validation logic for all messages.
     */
    @Override
    public final void validate() {
        if(getMessageType() == null) {
            throw new IllegalArgumentException("Message type must not be null");
        }

        validateSpecific();
    }

    /**
     * Build the common message attributes
     */
    protected Map<String, MessageAttributeValue> buildBaseAttributes(){
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put(MessageAttributeKey.MESSAGE_TYPE, MessageAttributeValue.builder()
                .stringValue(getMessageType().getType())
                .dataType("String")
                .build());

        attributes.put(MessageAttributeKey.SOURCE, MessageAttributeValue.builder()
                .stringValue(getSource())
                .dataType("String")
                .build());

        return attributes;
    }
}
