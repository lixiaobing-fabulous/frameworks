package com.lxb.aop.interceptor;


import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public abstract class InterceptorMethod {
    private Method method;
    private Object target;

    public InterceptorMethod(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    public abstract boolean supports(Method method, Object target);
}
