package com.lxb.aop.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.lxb.aop.advisor.Advisor;
import com.lxb.aop.joinpoint.ChainableMethodAopJoinPoint;

public class JdkInvocationHandler implements InvocationHandler {
    private final Object source;
    private final Object[] interceptors;

    public JdkInvocationHandler(Object source, Object[] interceptors) {
        this.source = source;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return new ChainableMethodAopJoinPoint(method, source, args,
                Arrays.stream(interceptors).map(Advisor.class::cast).toArray()).proceed();
    }
}
