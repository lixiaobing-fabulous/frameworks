package com.lxb.aop.annotation.processor;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Before;
import com.lxb.aop.interceptor.BeforeMethodInterceptor;
import com.lxb.aop.interceptor.MethodInterceptor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
@Component
public class BeforeAdvisorAdaptor extends BaseAnnotationAdvisorAdaptor {

    @Override
    public boolean support(Method method) {
        return method.isAnnotationPresent(Before.class);
    }


    @Override
    protected String getValue(Method method) {
        return method.getAnnotation(Before.class).value();
    }

    @Override
    protected MethodInterceptor getInterceptor(Method method, Object target) {
        return new BeforeMethodInterceptor(method, target);
    }

}
