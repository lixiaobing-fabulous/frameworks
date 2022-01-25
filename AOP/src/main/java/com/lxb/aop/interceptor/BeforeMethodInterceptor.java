package com.lxb.aop.interceptor;

import java.lang.reflect.Method;

import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class BeforeMethodInterceptor extends AbstractMethodInterceptor {
    public BeforeMethodInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object proceed(MethodAopJoinPoint joinpoint) throws Throwable {
        getMethod().invoke(getTarget());
        return joinpoint.proceed();
    }

}
