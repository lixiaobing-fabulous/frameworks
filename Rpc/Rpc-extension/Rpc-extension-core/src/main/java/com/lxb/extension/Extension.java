package com.lxb.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展实现注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    /**
     * 类型(名称)
     *
     * @return 类型(名称)
     */
    String value() default "";

    /**
     * 供应商
     *
     * @return 供应商
     */
    String provider() default "";

    /**
     * 排序顺序，按照优先级升序排序
     *
     * @return 排序优先级
     */
    int order() default Ordered.ORDER;

    /**
     * 单例标识
     *
     * @return 单例标识
     */
    boolean singleton() default true;
}
