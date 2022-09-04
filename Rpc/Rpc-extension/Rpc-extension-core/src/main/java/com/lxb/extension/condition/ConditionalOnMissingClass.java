package com.lxb.extension.condition;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnMissingClassCondition.class)
public @interface ConditionalOnMissingClass {

    /**
     * 类名
     */
    String[] value() default {};

}
