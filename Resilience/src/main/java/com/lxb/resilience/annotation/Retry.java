package com.lxb.resilience.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Retry {
    int maxRetries() default 3;

    long delay() default 0L;

    long maxDuration() default 180000L;

    long jitter() default 200L;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    Class<? extends Throwable>[] retryFor() default {Throwable.class};

    Class<? extends Throwable>[] skipFor() default {};
}
