package com.lxb.extension.condition;


import java.lang.annotation.Annotation;

/**
 * 条件匹配
 */
public interface Condition {

    /**
     * 匹配
     *
     * @param classLoader 类加载器
     * @param clazz       插件实现类
     * @param annotation  注解
     * @return 是否满足
     */
    boolean match(ClassLoader classLoader, Class clazz, Annotation annotation);

}
