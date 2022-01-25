package com.lxb.aop.joinpoint;

import java.lang.reflect.Method;

public interface MethodAopJoinPoint extends JoinPoint {


    Object[] getParameters();

    Method getMethod();

}
