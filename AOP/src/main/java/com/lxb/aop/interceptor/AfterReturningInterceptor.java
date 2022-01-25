package com.lxb.aop.interceptor;

import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AfterReturningInterceptor extends AbstractMethodInterceptor {
    public AfterReturningInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        Object result = joinpoint.proceed();
        getMethod().invoke(getTarget(), result);
        return result;
    }

}
