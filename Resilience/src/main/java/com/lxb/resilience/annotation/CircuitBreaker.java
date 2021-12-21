package com.lxb.resilience.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author lixiaobing
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface CircuitBreaker {

    Class<? extends Throwable>[] failOn() default {Throwable.class};

    Class<? extends Throwable>[] skipOn() default {};

    long delay() default 10000L;

    TimeUnit delayTimeUnit() default TimeUnit.MILLISECONDS;

    long minThreshold() default 10;

    double failureRatio() default 0.6;

    int successThreshold() default 2;
}
