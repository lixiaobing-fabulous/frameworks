package com.lxb.aop.jdk;

import com.lxb.aop.interceptor.InterceptorMethod;
import com.lxb.aop.joinpoint.ChainableMethodAopJoinPoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JdkInvocationHandler implements InvocationHandler {
    private final Object   source;
    private final Object[] interceptors;

    public JdkInvocationHandler(Object source, Object[] interceptors) {
        this.source = source;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<InterceptorMethod> interceptorMethods = Arrays.stream(interceptors).map(interceptor -> (InterceptorMethod) interceptor).filter(interceptorMethod -> interceptorMethod.supports(method, source)).collect(Collectors.toList());
        return new ChainableMethodAopJoinPoint(method, source, args, interceptorMethods.toArray()).proceed();
    }
}
