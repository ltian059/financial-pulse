package com.fp.sqs.service;

import com.fp.sqs.email.EmailMessage;

public interface EmailSqsService extends SqsService {

    /**
     * Send an email message to the email queue.
     *
     * @param emailMessage the email message to send
     */
    void sendEmailMessage(EmailMessage emailMessage);

    /**
     * Send an email message to a specific queue URL.
     *
     * @param emailMessage the email message to send
     * @param queueUrl the target queue URL
     */
    void sendEmailMessage(EmailMessage emailMessage, String queueUrl);
}
