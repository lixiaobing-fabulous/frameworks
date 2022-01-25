package com.lxb.aop.joinpoint;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.lxb.aop.advisor.Advisor;
import com.lxb.aop.interceptor.MethodInterceptor;


public class ChainableMethodAopJoinPoint implements MethodAopJoinPoint {
    private final Method method;
    private final Object target;
    private final Object[] parameters;

    private final List<Object> interceptors;
    private int pos;
    private final int size;

    public ChainableMethodAopJoinPoint(Method method, Object target, Object[] params, Object... interceptors) {
        this.method = method;
        this.target = target;
        this.parameters = params != null ? params : new Object[0];
        this.interceptors = Arrays.asList(interceptors);
        this.pos = 0;
        this.size = this.interceptors.size();
    }

    @Override
    public Object proceed() throws Throwable {
        if (pos < size) {
            int currentPos = pos++;
            Object interceptor = interceptors.get(currentPos);
            Advisor advisor = (Advisor) interceptor;
            PointCut pointCut = advisor.getPointCut();
            if (pointCut.matches(this.method, target.getClass())) {
                MethodInterceptor methodInterceptor = advisor.methodInterceptor();
                return methodInterceptor.proceed(this);
            } else {
                return proceed();
            }
        } else {
            return method.invoke(target, parameters);
        }
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}
