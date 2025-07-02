package com.fp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fp.lambda.enumeration.EmailType;
import com.fp.lambda.message.EmailMessage;
import com.fp.lambda.message.EmailMessageAttributeKey;
import com.fp.lambda.message.impl.FollowerNotificationMessage;
import com.fp.lambda.message.impl.VerificationEmailMessage;
import com.fp.lambda.service.LambdaEmailService;
import com.fp.lambda.service.impl.LambdaEmailServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Lambda function Entry point for processing email messages.
 *
 * Process SQS event and invoke email service to send emails based on the message type.
 */
@Slf4j
public class EmailProcessorHandler implements RequestHandler<SQSEvent, String> {

    private static final ObjectMapper objectMapper;
    private static final LambdaEmailService emailService;

    static {
        log.info("Initializing LambdaEmailService");
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        emailService = new LambdaEmailServiceImpl();
        log.info("LambdaEmailService initialized successfully");
    }

    @Override
    public String handleRequest(SQSEvent input, Context context) {
        log.info("Starting to process SQS event with {} messages", input.getRecords().size());
        int sucessCount = 0;
        int failureCount = 0;

        // Handle each SQS message
        for(SQSEvent.SQSMessage message :input.getRecords()){
            try {
                processMessage(message, context);
                sucessCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("Error processing message: {}", message.getBody(), e);
                // retry logic can be added here if needed
                throw new RuntimeException("Failed to process message: " + message.getMessageId(), e);
            }
        }
        String result = String.format("Successfully processed %d message; failed to process %d message", sucessCount, failureCount);
        log.info(result);
        return result;
    }

    /**
     * Logic to process each SQS message.
     */
    private void processMessage(SQSEvent.SQSMessage message, Context context) throws JsonProcessingException {
        log.info("Processing SQS messageId:{}, messageBody:{}", message.getMessageId(), message.getBody());

        // Parse the message body to EmailMessage Object
        Map<String, SQSEvent.MessageAttribute> attributes = message.getMessageAttributes();
        String messageType = attributes.get(EmailMessageAttributeKey.MESSAGE_TYPE).getStringValue();
        EmailType emailType = EmailType.fromString(messageType);

        EmailMessage parsedEmailMessage = mapToEmailMessageEntity(message.getBody(), emailType);
        // After get the message entity class, process it depending on its type.
        emailService.processMessage(parsedEmailMessage);


        log.info("Successfully processed email message: to {}, type:{}", parsedEmailMessage.getEmail(), emailType);
    }

    private EmailMessage mapToEmailMessageEntity(String body, EmailType emailType) throws JsonProcessingException {
        EmailMessage emailMessage = null;
        switch (emailType){
            case VERIFICATION -> emailMessage = objectMapper.readValue(body, VerificationEmailMessage.class);
            case FOLLOWER_NOTIFICATION ->  emailMessage = objectMapper.readValue(body, FollowerNotificationMessage.class);
        }
        if(emailMessage == null) {
            throw new IllegalArgumentException("Unsupported email type: " + emailType);
        }

        return emailMessage;

    }

}
