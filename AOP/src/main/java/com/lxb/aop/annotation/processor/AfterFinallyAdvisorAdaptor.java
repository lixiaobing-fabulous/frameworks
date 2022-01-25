package com.lxb.aop.annotation.processor;

import java.lang.reflect.Method;

import com.lxb.aop.annotation.AfterFinally;
import com.lxb.aop.interceptor.AfterFinallyInterceptor;
import com.lxb.aop.interceptor.MethodInterceptor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AfterFinallyAdvisorAdaptor extends BaseAnnotationAdvisorAdaptor {

    @Override
    public boolean support(Method method) {
        return method.isAnnotationPresent(AfterFinally.class);
    }


    @Override
    protected String getValue(Method method) {
        return method.getAnnotation(AfterFinally.class).value();
    }

    @Override
    protected MethodInterceptor getInterceptor(Method method, Object target) {
        return new AfterFinallyInterceptor(method, target);
    }
}
