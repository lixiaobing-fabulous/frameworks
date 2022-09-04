package com.lxb.extension.condition;


import java.lang.annotation.Annotation;

/**
 * 类存在判断
 */
public class OnClassCondition implements Condition {
    @Override
    public boolean match(final ClassLoader classLoader, final Class clazz, final Annotation annotation) {
        ConditionalOnClass onClass = (ConditionalOnClass) annotation;
        for (String name : onClass.value()) {
            if (name != null && !name.isEmpty()) {
                try {
                    Class.forName(name, false, classLoader);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
        return true;
    }
}
