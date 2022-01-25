package com.lxb.aop.joinpoint;

import java.lang.reflect.Method;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public interface MethodMatcher {

    default boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

}
