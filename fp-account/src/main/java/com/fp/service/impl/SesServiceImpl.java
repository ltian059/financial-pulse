package com.fp.service.impl;

import com.fp.entity.Account;
import com.fp.properties.SesProperties;
import com.fp.service.SesService;
import com.fp.constant.UrlConstant;
import com.fp.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "fp.aws.ses.enabled", havingValue = "true", matchIfMissing = true)
public class SesServiceImpl implements SesService {
    private final SesClient sesClient;
    private final JwtService jwtService;
    private final SesProperties sesProperties;

    @Override
    @Async
    public void sendVerificationEmail(Account account) {
        //1. Generate verification token using account info
        String verifyToken = jwtService.generateVerifyToken(account.getAccountId(), account.getEmail());
        //2. Construct verification link url
        String verifyUrl = sesProperties.getAppBaseUrl() + UrlConstant.VERIFY_TOKEN_ONLY_PATHS[0] + "?token=" + verifyToken;
        //3. Construct email content
        String subject = "Verify your Financial Pulse account";
        String htmlBody = buildVerifyEmailHtml(account.getName(), verifyUrl);
        String textBody = buildVerifyEmailText(account.getName(), verifyUrl);
        //4. Send the email using SES client
        //TODO: USE SQS to send email asynchronously
        sendEmail(account.getEmail(), subject, htmlBody, textBody);
        log.debug("Verification email sent successfully to: {}", account.getEmail());

    }


    /**
     * Builds the HTML content for the verification email.
     * @param name the name of the account holder
     * @param verifyUrl the verification URL to be included in the email
     * @return the HTML content as a String
     */
    private String buildVerifyEmailHtml(String name, String verifyUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify Your Email - Financial Pulse</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">📧 Verify Your Email</h1>
                </div>
                
                <div style="background: #ffffff; padding: 30px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 10px 10px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">Hi <strong>%s</strong>,</p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        Welcome to Financial Pulse! To complete your account setup and start connecting with other investors, 
                        please verify your email address by clicking the button below:
                    </p>
                    
                    <div style="text-align: center; margin: 35px 0;">
                        <a href="%s" style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 35px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold; font-size: 16px; transition: all 0.3s ease;">
                            Verify Email Address
                        </a>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 25px 0;">
                        <p style="margin: 0; font-size: 14px; color: #666;">
                            <strong>Security Note:</strong> This verification link will expire in 24 hours for your security.
                        </p>
                    </div>
                    
                    <p style="font-size: 14px; color: #666; margin-top: 25px;">
                        If the button doesn't work, copy and paste this link into your browser:
                    </p>
                    <p style="word-break: break-all; font-size: 12px; color: #007bff; background: #f1f3f4; padding: 10px; border-radius: 4px;">
                        %s
                    </p>
                    
                    <hr style="border: 1px solid #eee; margin: 30px 0;">
                    
                    <p style="font-size: 14px; color: #666; margin-bottom: 10px;">
                        Best regards,<br>
                        <strong>The Financial Pulse Team</strong>
                    </p>
                    
                    <p style="font-size: 12px; color: #999; margin-top: 20px;">
                        If you didn't create an account with Financial Pulse, please ignore this email.
                    </p>
                </div>
            </body>
            </html>
            """, name, verifyUrl, verifyUrl);
    }

    /**
     * Builds the plain text content for the verification email.
     * @param name the name of the account holder
     * @param verifyUrl the verification URL to be included in the email
     * @return the plain text content as a String
     */
    private String buildVerifyEmailText(String name, String verifyUrl) {
        return String.format("""
            Verify Your Email Address - Financial Pulse
            
            Hi %s,
            
            Welcome to Financial Pulse! To complete your account setup and start connecting with other investors, please verify your email address.
            
            Click or copy the following link into your browser:
            %s
            
            Security Note: This verification link will expire in 24 hours for your security.
            
            If you didn't create an account with Financial Pulse, please ignore this email.
            
            Best regards,
            The Financial Pulse Team
            """, name, verifyUrl);
    }

    /**
     * Core method to send an email using AWS SES.
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param htmlBody the HTML body of the email
     * @param textBody the plain text body of the email
     */
    private void sendEmail(String toEmail, String subject, String htmlBody, String textBody) {
        if (sesProperties.getFromEmail().equals("${AWS_SES_FROM_EMAIL}")) {
            throw new IllegalArgumentException("From email and name must be configured in application properties.");
        }
        String fromAddress = String.format("%s <%s>",
                sesProperties.getFromName(), sesProperties.getFromEmail());
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
