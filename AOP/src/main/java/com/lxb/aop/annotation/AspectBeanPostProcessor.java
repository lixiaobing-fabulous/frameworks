package com.lxb.aop.annotation;

import com.lxb.aop.DefaultProxyEnhancer;
import com.lxb.aop.interceptor.AnnotationInterceptorMethod;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AspectBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext                applicationContext;
    private List<AnnotationInterceptorMethod> allInterceptorMethods;

    @Autowired
    private DefaultProxyEnhancer defaultProxyEnhancer;

    @PostConstruct
    public void init() {
        Collection<Object> aspectObjs = applicationContext.getBeansWithAnnotation(Aspect.class).values();
        allInterceptorMethods = new ArrayList<>();
        for (Object aspectObj : aspectObjs) {
            Method[] methods = aspectObj.getClass().getMethods();
            List<AnnotationInterceptorMethod> interceptorMethods = Stream.of(methods).filter(method -> method.isAnnotationPresent(Around.class)).map(method -> {
                Around                      annotation      = method.getAnnotation(Around.class);
                Class<? extends Annotation> annotationClass = getAnnotationClass(annotation.value());
                return new AnnotationInterceptorMethod(method, aspectObj, annotationClass);
            }).collect(Collectors.toList());
            allInterceptorMethods.addAll(interceptorMethods);
        }

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        return defaultProxyEnhancer.proxy(bean, clazz, allInterceptorMethods);
    }

    @SneakyThrows
    private Class<? extends Annotation> getAnnotationClass(String value) {
        return (Class<? extends Annotation>) Class.forName(value);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
