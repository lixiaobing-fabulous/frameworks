package com.lxb.aop.interceptor;

import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AfterThrowingInterceptor extends AbstractMethodInterceptor {
    public AfterThrowingInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        Object result = null;
        try {
            result = joinpoint.proceed();
        } catch (Throwable throwable) {
            getMethod().invoke(getTarget(), throwable);
        }
        return result;
    }
}
