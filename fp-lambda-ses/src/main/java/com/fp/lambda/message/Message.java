package com.fp.lambda.message;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
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

    default Integer getPriority() {
        return 5;
    }

    default Integer getDelaySeconds() {
        return 0;
    }

    default Integer getRetryCount() {
        return 0;
    }


}
