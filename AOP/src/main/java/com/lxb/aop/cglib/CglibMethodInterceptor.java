package com.lxb.aop.cglib;

import com.lxb.aop.interceptor.InterceptorMethod;
import com.lxb.aop.joinpoint.ChainableMethodAopJoinPoint;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CglibMethodInterceptor implements MethodInterceptor {
    private final Object   source;
    private final Object[] interceptors;

    public CglibMethodInterceptor(Object source, Object[] interceptors) {
        this.source = source;
        this.interceptors = interceptors;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        List<InterceptorMethod> interceptorMethods = Arrays.stream(interceptors).map(interceptor -> (InterceptorMethod) interceptor).filter(interceptorMethod -> interceptorMethod.supports(method, source)).collect(Collectors.toList());
        return new ChainableMethodAopJoinPoint(method, target, args, interceptorMethods.toArray()).proceed();
    }
}
