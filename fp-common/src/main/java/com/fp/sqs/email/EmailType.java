package com.fp.sqs.email;

import com.fp.sqs.MessageType;

public enum EmailType implements MessageType {
    VERIFICATION("email.verification", "Verification Email"),
    FOLLOWER_NOTIFICATION("email.follower_notification", "Follower Notification Email"),
    WELCOME("email.welcome", "Welcome Email"),
    PASSWORD_RESET("email.password_reset", "Password Reset Email"),
    SYSTEM_NOTIFICATION("email.system_notification", "System Notification Email"),
    ;

    private final String type;
    private final String description;

    EmailType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static EmailType fromString(String type){
        for (EmailType emailType : EmailType.values()){
            if(emailType.type.equalsIgnoreCase(type)){
                return emailType;
            }
        }
        throw new IllegalArgumentException("Unknown email type: " + type);
    }
}
