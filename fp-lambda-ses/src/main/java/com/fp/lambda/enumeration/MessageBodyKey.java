package com.fp.lambda.enumeration;

import lombok.Getter;

@Getter
public enum MessageBodyKey {
    VERIFICATION_TOKEN("verification_token"),
    PASSWORD_RESET_TOKEN("password_reset_token"),
    ACCOUNT_ID("account_id"),
    ACCOUNT_NAME("name"),
    ACCOUNT_EMAIL("email"),

    FOLLOWER_NAME("follower_name"),

    ;


    private final String key;

    MessageBodyKey(String key) {
        this.key = key;
    }
}
