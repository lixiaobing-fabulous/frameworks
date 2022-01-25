package com.lxb.aop.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lxb.aop.advisor.Advisor;
import com.lxb.aop.advisor.DefaultAdvisor;
import com.lxb.aop.annotation.Order;
import com.lxb.aop.interceptor.MethodInterceptor;
import com.lxb.aop.joinpoint.AnnotationPointcut;

import lombok.SneakyThrows;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public abstract class BaseAnnotationAdvisorAdaptor implements AdvisorAdaptor {
    private final Pattern pattern = Pattern.compile("@annotation\\((.*)\\)");

    @Override
    public boolean support(Method method) {
        return false;
    }

    @Override
    @SneakyThrows
    public Advisor createAdvisor(Method method, Class<?> clazz, Object target) {
        MethodInterceptor afterFinallyInterceptor = getInterceptor(method, target);
        String value = getValue(method);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String annotationClass = matcher.group(1);
            Class<?> annotationClazz = Class.forName(annotationClass);
            AnnotationPointcut annotationPointcut = new AnnotationPointcut(
                    (Class<? extends Annotation>) annotationClazz);
            Order annotation = method.getAnnotation(Order.class);
            return new DefaultAdvisor(annotationPointcut, afterFinallyInterceptor,
                    Optional.ofNullable(annotation).map(Order::value).orElse(0));
        }
        return null;
    }

    protected abstract String getValue(Method method);

    protected abstract MethodInterceptor getInterceptor(Method method, Object target);
}
