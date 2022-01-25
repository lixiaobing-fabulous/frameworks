package com.lxb.aop.interceptor;


import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

import lombok.Getter;

@Getter
public abstract class AbstractMethodInterceptor implements MethodInterceptor {
    private Method method;
    private Object target;

    public AbstractMethodInterceptor(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        return method.invoke(target, joinpoint.getThis());
    }

}
