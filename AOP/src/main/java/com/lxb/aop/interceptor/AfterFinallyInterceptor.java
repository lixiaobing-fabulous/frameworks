package com.lxb.aop.interceptor;

import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AfterFinallyInterceptor extends AbstractMethodInterceptor {
    public AfterFinallyInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinpoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            getMethod().invoke(getTarget(), result, throwable);
        }
    }

}
