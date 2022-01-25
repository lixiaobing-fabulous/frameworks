package com.lxb.aop.annotation.processor;

import java.lang.reflect.Method;

import com.lxb.aop.annotation.AfterThrowing;
import com.lxb.aop.interceptor.AfterThrowingInterceptor;
import com.lxb.aop.interceptor.MethodInterceptor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AfterThrowingAdvisorAdaptor extends BaseAnnotationAdvisorAdaptor {
    @Override
    public boolean support(Method method) {
        return method.isAnnotationPresent(AfterThrowing.class);
    }

    @Override
    protected String getValue(Method method) {
        return method.getAnnotation(AfterThrowing.class).value();
    }

    @Override
    protected MethodInterceptor getInterceptor(Method method, Object target) {
        return new AfterThrowingInterceptor(method, target);
    }
}
