package com.lxb.aop.joinpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public class AnnotationPointcut implements PointCut {

    private Class<? extends Annotation> annotationClass;

    public AnnotationPointcut(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods()).anyMatch(method -> method.isAnnotationPresent(annotationClass));
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return method.isAnnotationPresent(annotationClass);
    }
}
