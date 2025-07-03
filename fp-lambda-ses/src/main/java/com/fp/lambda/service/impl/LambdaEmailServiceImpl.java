package com.fp.lambda.service.impl;

import com.fp.lambda.message.EmailMessage;
import com.fp.lambda.message.impl.FollowerNotificationMessage;
import com.fp.lambda.message.impl.VerificationEmailMessage;
import com.fp.lambda.service.LambdaEmailService;
import com.fp.lambda.util.EmailTemplate;
import com.fp.lambda.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
public class LambdaEmailServiceImpl implements LambdaEmailService {
    private final SesClient sesClient;
    private final String fromEmail;
    private final String fromName;
    private final String appBaseUrl;

    public LambdaEmailServiceImpl(ApplicationConfig applicationConfig) {
        ApplicationConfig.LambdaConfig lambdaConfig = applicationConfig.getLambda();
        ApplicationConfig.ServicesConfig services = applicationConfig.getServices();
        String region = lambdaConfig.getSes().getRegion();

        this.fromEmail = lambdaConfig.getSes().getFromEmail();
        this.fromName = lambdaConfig.getSes().getFromName();
        this.appBaseUrl = services.getAccountUrl();

        if(fromEmail == null || fromEmail.isEmpty()){
            throw new IllegalArgumentException("FROM_EMAIL environment variable is required");
        }
        sesClient = SesClient.builder()
                .build();
    }

    @Override
    public void processMessage(com.fp.lambda.message.Message message) {
        log.debug("Processing email message: {}", message.getMessageType());
        if(message instanceof EmailMessage emailMessage) {
            switch (emailMessage.getEmailType()){
                case VERIFICATION -> sendVerificationEmail(emailMessage);
                case WELCOME -> sendWelcomeEmail(emailMessage);
                case FOLLOWER_NOTIFICATION -> sendFollowerNotificationEmail(emailMessage);
//                case PASSWORD_RESET -> sendPasswordResetEmail(emailMessage);
                case SYSTEM_NOTIFICATION -> sendSystemNotificationEmail(emailMessage);
                default -> {
                    log.error("Unsupported email type: {}", emailMessage.getEmailType());
                    throw new IllegalArgumentException("Unknown email type" + emailMessage.getEmailType());
                }
            }
        }else {
            log.error("Invalid message type: {}", message.getClass().getName());
            throw new IllegalArgumentException("Invalid message type: " + message.getClass().getName());
        }
    }


    private void sendSystemNotificationEmail(EmailMessage emailMessage) {

    }

//    private void sendPasswordResetEmail(EmailMessage emailMessage) {
//        String subject = "Password Reset Request";
//        if (emailMessage instanceof PasswordResetMessage passwordResetMessage){
//            String resetPasswordToken = passwordResetMessage.getResetPasswordToken();
//            String resetPasswordUrl = buildPasswordResetURL(resetPasswordToken);
//            String htmlBody = EmailTemplate.buildPasswordResetEmailHtml(emailMessage.getName(), resetPasswordUrl);
//            String textBody = EmailTemplate.buildPasswordResetEmailText(emailMessage.getName(), resetPasswordUrl);
//            sendEmailViaSES(emailMessage.getEmail(), subject, htmlBody, textBody);
//            log.debug("Password reset email sent successfully to: {}", emailMessage.getEmail());
//        }else{
//            log.error("Invalid message type for password reset: {}", emailMessage.getClass().getName());
//            throw new IllegalArgumentException("Invalid message type for password reset: " + emailMessage.getClass().getName());
//        }
//    }

    private void sendFollowerNotificationEmail(EmailMessage emailMessage) {
        if(emailMessage instanceof FollowerNotificationMessage followerNotificationMessage) {
            String subject = "New Follower Notification";
            String followerName = followerNotificationMessage.getFollowerName();
            String htmlBody = EmailTemplate.buildFollowerNotificationEmailHtml(followerNotificationMessage.getName(), followerName);
            String textBody = EmailTemplate.buildFollowerNotificationEmailText(followerNotificationMessage.getName(), followerName);
            sendEmailViaSES(followerNotificationMessage.getEmail(), subject, htmlBody, textBody);
            log.debug("Follower notification email sent successfully to: {}", emailMessage.getEmail());
        }else{
            log.error("Invalid message type: {}", emailMessage.getClass().getName());
            throw new IllegalArgumentException("Invalid message type for follower notification: " + emailMessage.getClass().getName());
        }
    }

    private void sendWelcomeEmail(EmailMessage emailMessage) {
        String subject = "Welcome to Financial Pulse!";
        String htmlBody = EmailTemplate.buildWelcomeEmailHtml(emailMessage.getName());
        String textBody = EmailTemplate.buildWelcomeEmailText(emailMessage.getName());
        sendEmailViaSES(emailMessage.getEmail(), subject, htmlBody, textBody);
    }


    public void sendVerificationEmail(EmailMessage emailMessage) {
        //1. Generate verification token from template data
        if(emailMessage instanceof VerificationEmailMessage verificationEmailMessage) {
            String verificationToken = verificationEmailMessage.getVerificationToken();
            if(verificationToken == null || verificationToken.isEmpty()){
                throw new IllegalArgumentException("Verification token is required");
            }
            //2. Construct verification link url
            String verifyUrl = buildVerifyURL(verificationToken);
            //3. Construct email content
            String subject = "Verify your Financial Pulse account";
            String htmlBody = EmailTemplate.buildVerifyEmailHtml(emailMessage.getName(), verifyUrl);
            String textBody = EmailTemplate.buildVerifyEmailText(emailMessage.getName(), verifyUrl);
            //4. Send the email using SES client
            sendEmailViaSES(emailMessage.getEmail(), subject, htmlBody, textBody);
            log.debug("Verification email sent successfully to: {}", emailMessage.getEmail());
        }else{
            log.error("Invalid email message type for verification: {}", emailMessage.getClass().getName());
            throw new IllegalArgumentException("Invalid email message type for verification: " + emailMessage.getClass().getName());
        }

    }

    private String buildVerifyURL(String verifyToken) {
        String baseURL = appBaseUrl != null ? appBaseUrl : "http://localhost:8080"; // Default to localhost if not set
        return baseURL + "/api/auth/verify?token=" + verifyToken;
    }

    private String buildPasswordResetURL(String resetPasswordToken) {
        String baseURL = appBaseUrl != null ? appBaseUrl : "http://localhost:8080"; // Default to localhost if not set
        return baseURL + "/api/auth/reset-password?token=" + resetPasswordToken;
    }

    /**
     * Core method to send an email using AWS SES.
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param htmlBody the HTML body of the email
     * @param textBody the plain text body of the email
     */
    private void sendEmailViaSES(String toEmail, String subject, String htmlBody, String textBody) {
        if (fromEmail.equals("${AWS_SES_FROM_EMAIL}")) {
            throw new IllegalArgumentException("From email and name must be configured in application properties.");
        }
        String fromAddress = String.format("%s <%s>",
                fromName, fromEmail);
        //Build destination
        Destination destination = Destination.builder()
                .toAddresses(toEmail)
                .build();
        //Build subject
        Content subjectContent = Content.builder()
                .data(subject)
                .charset("UTF-8")
                .build();

        //Build Body
        Content htmlContent = Content.builder()
                .data(htmlBody)
                .charset("UTF-8")
                .build();

        Content textContent = Content.builder()
                .data(textBody).charset("UTF-8").build();

        Body body = Body.builder()
                .html(htmlContent)
                .text(textContent)
                .build();

        //Build a message
        Message message = Message.builder()
                .body(body)
                .subject(subjectContent)
                .build();
        //Build and send the email request
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(fromAddress)
                .message(message)
                .destination(destination)
                .build();
        SendEmailResponse response = sesClient.sendEmail(sendEmailRequest);
        log.debug("Email sent successfully to {} with message ID: {}", toEmail, response.messageId());
    }
}
