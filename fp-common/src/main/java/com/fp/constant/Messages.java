package com.fp.constant;

public final class Messages {
    private Messages() {}

    public static final class Error {
        private Error() {}
        //Account related error messages
        public static final class Account {
            public static final String ALREADY_EXISTS = "Account already exists.";
            public static final String EMAIL_SENDING_ERROR = "Error sending verification email.";
            public static final String JWT_CONTEXT_ERROR = "JWT Authentication context is invalid. Please try to re-login.";
            public static final String ALREADY_VERIFIED = "Account already verified.";
            public static final String BIRTHDAY_FORMAT_ERROR = "Birthday format error. Please check your birthday format is MM/dd/yyyy and the value is correct.";

            private Account() {}
            public static final String NOT_FOUND = "Account not found.";
            public static final String INVALID_PASSWORD = "Invalid password.";
            public static final String USER_ALREADY_FOLLOWED = "You have already followed this user";

            //Template methods
            public static String notFoundById(String accountId){
                return String.format("Account with id %d not found.", accountId);
            }
            public static String notFoundByEmail(String email) {
                return String.format("Account with email %s not found.", email);
            }

        }

        //Authentication related error messages
        public static final class Auth {
            public static final String ACCESS_TOKEN_NOT_ALLOWED_ON_PATH = "Access token cannot be used for this endpoint.";
            public static final String VERIFICATION_TOKEN_NOT_ALLOWED_ON_PATH = "Verification token cannot be used for this endpoint.";
            public static final String REFRESH_TOKEN_NOT_ALLOWED_ON_PATH = "Refresh token cannot be used for this endpoint.";
            public static final String JWT_REVOKED = "JWT token has been revoked.";

            private Auth() {}
            public static final String INVALID_TOKEN_TYPE = "Invalid token type for this request";

            public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
            public static final String TOKEN_EXPIRED = "Authentication token has expired";
            public static final String ACCESS_DENIED = "Access denied: insufficient permissions";
            public static final String SESSION_EXPIRED = "Your session has expired. Please log in again.";
            public static final String INVALID_TOKEN = "Invalid token provided.";
            public static final String UNAUTHORIZED = "Unauthorized access.";
            public static String unauthorized(String message) {
                return String.format("Unauthorized access: %s", message);
            }
            public static String accessDenied(String message) {
                return String.format("Access denied: %s", message);
            }
        }

        //Follow related error messages
        public static final class Follow {
            private Follow() {}
            public static final String FOLLOW_NOT_FOUND = "Follow relationship not found.";
            public static final String FOLLOW_ALREADY_EXISTS = "Follow relationship already exists.";
            public static final String FOLLOW_NOT_ALLOWED = "You cannot follow this user.";
            public static final String SELF_FOLLOW_NOT_ALLOWED = "You cannot follow yourself.";
        }
    }


    public static final class Success {
        private  Success() {}
        public static final class Account {
            public static final String CREATED = "Account created successfully";
            public static final String VERIFIED = "Account verified successfully";
            public static final String UPDATED = "Account updated successfully";
            public static final String LOGIN = "Login successful";
            public static final String VERIFICATION_EMAIL_SENT = "Verification email sent successfully";
            public static final Object DELETED = "Account deleted successfully";

            public static String welcomeAfterAccountCreation(String name) {
                return String.format("Welcome %s! Your account has been created successfully.", name);
            }
        }

        public static final class Follow {
            public static final String FOLLOWED_SUCCESSFULLY = "User followed successfully";
            public static final String UNFOLLOWED_SUCCESSFULLY = "User unfollowed successfully";
        }

        public static final class Content {

        }
    }
}
