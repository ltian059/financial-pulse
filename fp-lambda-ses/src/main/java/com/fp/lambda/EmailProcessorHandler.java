package com.fp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
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
import com.fp.lambda.config.ApplicationConfig;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lambda function Entry point for processing email messages.
 *
 * Process SQS event and invoke email service to send emails based on the message type.
 */
public class EmailProcessorHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final ObjectMapper objectMapper;
    private static final LambdaEmailService emailService;
    private static final org.slf4j.Logger log;

    static {
        try {
            //Load configuration from application.yml
            ApplicationConfig applicationConfig = ApplicationConfig.load();
            ApplicationConfig.LambdaConfig lambdaConfig = applicationConfig.getLambda();
            applicationConfig.validate();
            //Configure logging
            configureLogging(applicationConfig);
            log = org.slf4j.LoggerFactory.getLogger(EmailProcessorHandler.class);
            //Initialize services
            emailService = new LambdaEmailServiceImpl(applicationConfig);
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
//            testLogLevel();
            log.debug("LambdaEmailService initialized successfully");
        }catch (Exception e){
            System.out.println("Failed to initialize EmailProcessorHandler: "+ e.getMessage());
            throw new RuntimeException("Failed to initialize EmailProcessorHandler", e);
        }
    }

    private static void testLogLevel() {
        System.out.println("=== Log Level Test ===");
        System.out.println("DEBUG enabled: " + log.isDebugEnabled());
        System.out.println("INFO enabled: " + log.isInfoEnabled());
        System.out.println("WARN enabled: " + log.isWarnEnabled());
        System.out.println("ERROR enabled: " + log.isErrorEnabled());
    }

    private static void configureLogging(ApplicationConfig config) {
        ApplicationConfig.LoggingConfig logging = config.getLogging();
        String logLevel = logging.getLevel();
        String rootLogLevel = logging.getRootLevel();

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel",
                rootLogLevel != null ? rootLogLevel.toLowerCase() : "warn");
        System.setProperty("org.slf4j.simpleLogger.log.com.fp",
                logLevel != null ? logLevel.toLowerCase() : "info");
        System.setProperty("org.slf4j.simpleLogger.log.software.amazon.awssdk", "warn");
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent input, Context context) {
        log.info("Batch size: {}", input.getRecords().size());
        List<SQSBatchResponse.BatchItemFailure> batchItemFailures = new ArrayList<>();

        log.debug("Starting to process SQS event with {} messages", input.getRecords().size());
        int successCount = 0;
        int failureCount = 0;
        // Handle each SQS message
        for(SQSEvent.SQSMessage message :input.getRecords()){
            try {
                processMessage(message,context);
                successCount++;
            }catch (Exception e){
                log.error("Failed to process message: {}, error: {}", message.getMessageId(), e.getMessage());
                //Put failed messages into batchItemFailures. SQS will retry the message later.
                batchItemFailures.add(SQSBatchResponse.BatchItemFailure.builder()
                        .withItemIdentifier(message.getMessageId())
                        .build()
                );
                failureCount++;
            }
        }
        String result = String.format("Successfully processed %d message; failed to process %d message", successCount, failureCount);
        log.info(result);
        return SQSBatchResponse.builder().withBatchItemFailures(batchItemFailures).build();
    }

    private boolean processMessageWithRetry(SQSEvent.SQSMessage message, Context context) {
        Map<String, SQSEvent.MessageAttribute> attributes = message.getMessageAttributes();
        int maxRetries = 3; // Default retry count
        if(attributes != null && attributes.containsKey(EmailMessageAttributeKey.RETRY_COUNT)){
            try {
                maxRetries = Integer.parseInt(attributes.get(EmailMessageAttributeKey.RETRY_COUNT).getStringValue());
            } catch (NumberFormatException e) {
                log.warn("Invalid retry count attribute, using default value: {}", maxRetries);
            }
        }
        for(int retryCount = 0; retryCount < maxRetries; retryCount++){
            try {
                processMessage(message, context);
                if(retryCount > 0){
                    log.info("Message processed successfully on retry {}/{}", retryCount, maxRetries);
                }
                return true;
            } catch (Exception e) {
                if(retryCount == maxRetries - 1){
                    log.error("Max retries reached for messageId: {}, body: {}, giving up", message.getMessageId(), message.getBody(), e);
                    return false;
                }else{
                    log.warn("Retrying messageId: {}, body: {}, retryCount: {}/{}",
                            message.getMessageId(), message.getBody(), retryCount + 1, maxRetries);
                }
            }
        }

        return false;
    }


    /**
     * Logic to process each SQS message.
     */
    private void processMessage(SQSEvent.SQSMessage message, Context context) {
        log.debug("Processing SQS messageId:{}, messageBody:{}", message.getMessageId(), message.getBody());
        try {
            Map<String, SQSEvent.MessageAttribute> attributes = message.getMessageAttributes();
            // Parse the message body to EmailMessage Object
            String messageType = attributes.get(EmailMessageAttributeKey.MESSAGE_TYPE).getStringValue();
            EmailType emailType = EmailType.fromString(messageType);
            EmailMessage parsedEmailMessage = mapToEmailMessageEntity(message.getBody(), emailType);
            // After get the message entity class, process it depending on its type.
            emailService.processMessage(parsedEmailMessage);
            log.debug("Successfully processed email message: to {}, type:{}", parsedEmailMessage.getEmail(), emailType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            log.error("Failed to process message: messageId={}, body={}", message.getMessageId(), message.getBody(), e);
            throw new RuntimeException("Failed to process message", e);
        }

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
