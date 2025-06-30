package com.fp.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate JWT context with request parameters.
 * <p>
 * This annotation ensures that the account ID and email from JWT context
 * match the corresponding values in the request parameters.
 * <p></p>
 * Enhanced annotation for JWT context validation with additional features.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RevokeJwt {

    /**
     * The name of the JWT token parameter to revoke (only for method-level annotation).
     * If empty, uses JWT from security context.
     * Ignored when annotation is on parameter.
     */
    String tokenName() default "";

    /**
     * Shorthand for revokeTokenAfter when only timing is specified.
     * Usage: @RevokeJwt(SUCCESS) or @RevokeJwt(FAILURE)
     */
    @AliasFor("revokeTokenAfter")
    RevokeTokenAfter value() default RevokeTokenAfter.SUCCESS;

    /**
     * Whether to automatically revoke JWT tokens after successful operation.
     * Useful for logout, delete account operations.
     */
    RevokeTokenAfter revokeTokenAfter() default RevokeTokenAfter.SUCCESS;

    /**
     * Reason for token revocation (optional).
     */
    String reason() default "";

    enum RevokeTokenAfter {
        /**
         * Whether to automatically revoke JWT tokens after successful operation.
         * Useful for logout, delete account operations.
         */
        SUCCESS,
        /**
         * Whether to revoke JWT tokens after a failed method execution(not returning).
         */
        FAILURE,
        /**
         * Whether to revoke JWT tokens after the method, regardless of success or failure.(finally)
         */
        ANY
    }
}
