package com.fp.lambda.message.impl;

import com.fp.lambda.enumeration.EmailType;
import com.fp.lambda.message.EmailMessage;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationEmailMessage implements EmailMessage {

    private String verificationToken;
    private String accountId;
    private String email;
    private String name;


    @Override
    public EmailType getEmailType() {
        return EmailType.VERIFICATION;
    }


    @Override
    public EmailType getMessageType() {
        return EmailType.VERIFICATION;
    }


}
