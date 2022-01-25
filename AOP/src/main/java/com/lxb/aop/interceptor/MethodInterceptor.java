package com.lxb.aop.interceptor;


import com.lxb.aop.joinpoint.MethodAopJoinPoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public interface MethodInterceptor {

    Object proceed(MethodAopJoinPoint joinpoint) throws Throwable;
}
