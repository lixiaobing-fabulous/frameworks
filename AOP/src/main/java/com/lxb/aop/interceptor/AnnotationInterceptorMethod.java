package com.lxb.aop.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationInterceptorMethod extends InterceptorMethod {
    private final Class<? extends Annotation> annotationClass;

    public AnnotationInterceptorMethod(Method method, Object target, Class<? extends Annotation> annotationClass) {
        super(method, target);
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean supports(Method method, Object target) {
        return method.isAnnotationPresent(annotationClass);
    }
}
