package com.fp.pattern.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

///
/// # Annotation to mark a class as a strategy component.
///
/// This is convenient for Spring to automatically detect and register strategy implements.
///
///
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyComponent {
    /**
     * The name of the strategy component.
     */
    String value() default "";

    /**
     * The priority of the strategy component.
     */
    int priority() default Integer.MAX_VALUE;

    String description() default "";
}
