package com.lxb.aop.annotation.processor;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.AfterReturning;
import com.lxb.aop.interceptor.AfterReturningInterceptor;
import com.lxb.aop.interceptor.MethodInterceptor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
@Component
public class AfterReturningAdvisorAdaptor extends BaseAnnotationAdvisorAdaptor {

    @Override
    public boolean support(Method method) {
        return method.isAnnotationPresent(AfterReturning.class);
    }

    @Override
    protected String getValue(Method method) {
        return method.getAnnotation(AfterReturning.class).value();
    }

    @Override
    protected MethodInterceptor getInterceptor(Method method, Object target) {
        return new AfterReturningInterceptor(method, target);
    }

}
