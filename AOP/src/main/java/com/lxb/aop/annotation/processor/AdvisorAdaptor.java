package com.lxb.aop.annotation.processor;

import java.lang.reflect.Method;

import com.lxb.aop.advisor.Advisor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public interface AdvisorAdaptor {
    boolean support(Method method);

    Advisor createAdvisor(Method method, Class<?> clazz, Object target);
}
