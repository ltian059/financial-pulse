package com.fp.sqs.impl;

import com.fp.sqs.MessageType;
import com.fp.sqs.email.AbstractEmailMessage;
import com.fp.sqs.email.EmailMessageBodyKey;
import com.fp.sqs.email.EmailType;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class VerificationEmailMessage extends AbstractEmailMessage {

    private String verificationToken;
    private String accountId;
    private String email;
    private String name;
    private String source;

    @Override
    public MessageType getMessageType() {
        return EmailType.VERIFICATION;
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.VERIFICATION;
    }


    @Override
    public void validateEmailSpecific() {
        if (this.verificationToken == null ||  verificationToken.isEmpty()) {
            throw new IllegalArgumentException("Verification token must not be null or empty for verification email message");
        }
    }


    public Map<String, Object> getMessageBody() {
        Map<String, Object> body = new HashMap<>();
        body.put(EmailMessageBodyKey.ACCOUNT_ID, accountId);
        body.put(EmailMessageBodyKey.NAME, name);
        body.put(EmailMessageBodyKey.EMAIL, email);
        body.put(EmailMessageBodyKey.VERIFICATION_TOKEN, verificationToken);
        return body;
    }


}
