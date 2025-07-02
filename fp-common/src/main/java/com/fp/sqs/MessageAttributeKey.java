package com.fp.sqs;

public class MessageAttributeKey {
    public static final String MESSAGE_TYPE = "messageType";
    public static final String PRIORITY = "priority";
    public static final String DELAY_SECONDS = "delaySeconds";
    public static final String RETRY_COUNT = "retryCount";
    public static final String SOURCE = "source";

    public static class EmailMessage {
        public static final String DOMAIN = "domain";
    }
}
