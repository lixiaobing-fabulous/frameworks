package com.lxb.resilience.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lixiaobing
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Fallback {

    String fallbackMethod() default "";

    Class<? extends Throwable>[] fallbackFor() default {Throwable.class};

    Class<? extends Throwable>[] skipFor() default {};
}
