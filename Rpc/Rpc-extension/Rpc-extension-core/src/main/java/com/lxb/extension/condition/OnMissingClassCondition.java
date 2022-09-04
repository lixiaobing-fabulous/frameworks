package com.lxb.extension.condition;


import java.lang.annotation.Annotation;

/**
 * 类不存在判断
 */
public class OnMissingClassCondition implements Condition {
    @Override
    public boolean match(final ClassLoader classLoader, final Class clazz, final Annotation annotation) {
        ConditionalOnClass onClass = (ConditionalOnClass) annotation;
        for (String name : onClass.value()) {
            if (name != null && !name.isEmpty()) {
                try {
                    Class.forName(name, false, classLoader);
                    return false;
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return true;
    }
}
