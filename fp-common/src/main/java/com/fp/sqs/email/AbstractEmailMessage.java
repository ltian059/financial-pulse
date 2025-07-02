package com.fp.sqs.email;

import com.fp.sqs.AbstractMessage;
import com.fp.sqs.MessageAttributeKey;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;

/**
 * AbstractEmailMessage serves as a base class for email messages in the SQS system.
 * It extends AbstractMessage and implements EmailMessage interface.
 */
public abstract class AbstractEmailMessage extends AbstractMessage implements EmailMessage {
    @Override
    protected void validateSpecific() {
        if(getAccountId() == null || getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be null or empty");
        }
        if(getEmail() == null || getEmail().isEmpty()) {
            throw new IllegalArgumentException("The recipient email must not be null or empty");
        }
        if(getName() == null || getName().isEmpty()) {
            throw new IllegalArgumentException("The recipient name must not be null or empty");
        }

        //Validate based on the specific email message class
        validateEmailSpecific();
    }

    protected abstract void validateEmailSpecific();

    @Override
    public Map<String, MessageAttributeValue> getMessageAttributes() {
        Map<String, MessageAttributeValue> attributes = buildBaseAttributes();

        //Add email-message-specific attributes...
        if(getEmail().contains("@")){
            String domain = getEmail().substring(getEmail().indexOf("@") + 1);
            attributes.put(MessageAttributeKey.EmailMessage.DOMAIN,
                MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(domain)
                    .build());
        }

        return attributes;
    }
}
