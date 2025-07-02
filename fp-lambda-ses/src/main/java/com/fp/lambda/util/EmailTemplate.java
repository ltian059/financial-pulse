package com.fp.lambda.util;

public class EmailTemplate {

    /**
     * Builds the HTML content for the verification email.
     * @param name the name of the account holder
     * @param verifyUrl the verification URL to be included in the email
     * @return the HTML content as a String
     */
    public static String buildVerifyEmailHtml(String name, String verifyUrl) {
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
    public static String buildVerifyEmailText(String name, String verifyUrl) {
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

    public static String buildWelcomeEmailHtml(String name) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to Financial Pulse</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">🎉 Welcome to Financial Pulse!</h1>
                </div>
                
                <div style="background: #ffffff; padding: 30px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 10px 10px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">Hi <strong>%s</strong>,</p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        Your account has been verified! Welcome to Financial Pulse.
                    </p>
                    
                    <div style="text-align: center; margin: 35px 0;">
                        <a href="http://localhost:3000/dashboard" style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 35px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold; font-size: 16px;">
                            Go to Dashboard
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """, name);
    }

    public static String buildWelcomeEmailText(String name) {
        return String.format("""
            Welcome to Financial Pulse!
            
            Hi %s,
            
            Your account has been verified! Welcome to Financial Pulse.
            
            Visit: http://localhost:3000/dashboard
            
            Best regards,
            The Financial Pulse Team
            """, name);
    }
    public static String buildFollowerNotificationEmailHtml(String userName, String followerName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>New Follower - Financial Pulse</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">👥 New Follower!</h1>
                </div>
                
                <div style="background: #ffffff; padding: 30px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 10px 10px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">Hi <strong>%s</strong>,</p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        <strong>%s</strong> is now following you on Financial Pulse!
                    </p>
                </div>
            </body>
            </html>
            """, userName, followerName);
    }
    public static String buildFollowerNotificationEmailText(String userName, String followerName) {
        return String.format("""
            New Follower - Financial Pulse
            
            Hi %s,
            
            %s is now following you on Financial Pulse!
            
            Best regards,
            The Financial Pulse Team
            """, userName, followerName);
    }

    public static String buildPasswordResetEmailHtml(String name, String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Reset - Financial Pulse</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">🔐 Password Reset</h1>
                </div>
                
                <div style="background: #ffffff; padding: 30px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 10px 10px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">Hi <strong>%s</strong>,</p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        Click the button below to reset your password:
                    </p>
                    
                    <div style="text-align: center; margin: 35px 0;">
                        <a href="%s" style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 35px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold; font-size: 16px;">
                            Reset Password
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """, name, resetUrl);
    }

    public static String buildPasswordResetEmailText(String name, String resetUrl) {
        return String.format("""
            Password Reset - Financial Pulse
            
            Hi %s,
            
            Click this link to reset your password: %s
            
            Best regards,
            The Financial Pulse Team
            """, name, resetUrl);
    }
}
