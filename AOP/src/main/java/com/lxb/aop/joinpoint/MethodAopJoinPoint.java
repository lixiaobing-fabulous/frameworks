package com.lxb.aop.joinpoint;

import java.lang.reflect.Method;

public interface MethodAopJoinPoint {


    Object proceed() throws Throwable;

    Object getTarget();

    Object[] getParameters();

    Method getMethod();

}
