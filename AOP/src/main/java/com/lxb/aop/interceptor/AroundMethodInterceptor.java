package com.lxb.aop.interceptor;

import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AroundMethodInterceptor implements MethodInterceptor {
    private Method method;
    private Object target;

    public AroundMethodInterceptor(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        return method.invoke(target, joinpoint);
    }

}
