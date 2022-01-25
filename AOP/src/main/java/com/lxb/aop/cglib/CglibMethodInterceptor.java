package com.lxb.aop.cglib;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.lxb.aop.advisor.Advisor;
import com.lxb.aop.joinpoint.ChainableMethodAopJoinPoint;

import net.sf.cglib.proxy.MethodProxy;

public class CglibMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor {
    private final Object source;
    private final Object[] interceptors;

    public CglibMethodInterceptor(Object source, Object[] interceptors) {
        this.source = source;
        this.interceptors = interceptors;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return new ChainableMethodAopJoinPoint(method, source, args,
                Arrays.stream(interceptors).map(Advisor.class::cast).toArray()).proceed();
    }
}
