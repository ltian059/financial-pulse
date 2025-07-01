package com.fp.sqs;

import com.fp.sqs.constant.EmailMessageBodyKey;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class VerificationEmailMessage implements EmailMessage {

    private String verificationToken;
    private String accountId;
    private String email;
    private String name;
    private String source;

    @Override
    public EmailType getEmailType() {
        return EmailType.VERIFICATION;
    }


    @Override
    public void validate() {
        if (this.verificationToken == null ||  verificationToken.isEmpty()) {
            throw new IllegalArgumentException("Verification token must not be null or empty for verification email message");
        }
        EmailMessage.super.validate();
    }


    @Override
    public Map<String, Object> getMessageBody() {
        Map<String, Object> body = new HashMap<>();
        body.put(EmailMessageBodyKey.ACCOUNT_ID, accountId);
        body.put(EmailMessageBodyKey.NAME, name);
        body.put(EmailMessageBodyKey.EMAIL, email);
        body.put(EmailMessageBodyKey.VERIFICATION_TOKEN, verificationToken);
        return body;
    }

    @Override
    public EmailType getMessageType() {
        return EmailType.VERIFICATION;
    }


}
