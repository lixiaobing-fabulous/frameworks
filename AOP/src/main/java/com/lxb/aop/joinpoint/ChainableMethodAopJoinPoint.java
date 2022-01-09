package com.lxb.aop.joinpoint;

import com.lxb.aop.interceptor.InterceptorMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ChainableMethodAopJoinPoint implements MethodAopJoinPoint {
    private final Method   method;
    private final Object   target;
    private final Object[] parameters;

    private final List<Object> interceptors;
    private       int          pos;
    private final int          size;

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
            int               currentPos        = pos++;
            Object            interceptor       = interceptors.get(currentPos);
            InterceptorMethod interceptorMethod = (InterceptorMethod) interceptor;
            return interceptorMethod.getMethod().invoke(interceptorMethod.getTarget(), this);
        }
        return method.invoke(target, parameters);
    }

    @Override
    public Object getTarget() {
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
