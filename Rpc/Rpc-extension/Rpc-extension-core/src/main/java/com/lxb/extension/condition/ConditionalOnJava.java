package com.lxb.extension.condition;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnJavaCondition.class)
public @interface ConditionalOnJava {

    /**
     * 范围
     */
    Range range() default Range.EQUAL_OR_NEWER;

    /**
     * 版本
     */
    String value();

    /**
     * Range options.
     */
    enum Range {

        /**
         * 等于或大于指定版本
         */
        EQUAL_OR_NEWER,

        /**
         * 小于指定版本.
         */
        OLDER_THAN

    }

}
