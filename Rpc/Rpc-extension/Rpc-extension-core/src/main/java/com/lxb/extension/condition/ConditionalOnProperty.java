package com.lxb.extension.condition;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {
    /**
     * 值
     *
     * @return 值
     */
    String value() default "";

    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 不存在是否匹配
     *
     * @return 不存在是否匹配标识
     */
    boolean matchIfMissing() default false;

}
