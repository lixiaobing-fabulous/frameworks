package com.lxb.aop.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.lxb.aop.DefaultProxyEnhancer;
import com.lxb.aop.advisor.Advisor;
import com.lxb.aop.annotation.processor.AdvisorAdaptor;
import com.lxb.aop.annotation.processor.AdvisorAdaptorRegistry;

import lombok.SneakyThrows;

@Component
public class AspectBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private List<Advisor> allAdvisors;

    @Autowired
    private DefaultProxyEnhancer defaultProxyEnhancer;

    @PostConstruct
    public void init() {
        Collection<Object> aspectObjs = applicationContext.getBeansWithAnnotation(Aspect.class).values();
        AdvisorAdaptorRegistry advisorAdaptorRegistry = new AdvisorAdaptorRegistry();
        List<AdvisorAdaptor> adaptors = advisorAdaptorRegistry.getAdaptors();
        allAdvisors = new ArrayList<>();
        for (Object aspectObj : aspectObjs) {
            Method[] methods = aspectObj.getClass().getMethods();
            for (Method method : methods) {
                for (AdvisorAdaptor adaptor : adaptors) {
                    if (adaptor.support(method)) {
                        Advisor advisor = adaptor.createAdvisor(method, aspectObj.getClass(), aspectObj);
                        if (Objects.nonNull(advisor)) {
                            allAdvisors.add(advisor);
                        }
                    }
                }
            }
        }
        allAdvisors.sort(Comparator.comparingInt(Advisor::priority));
    }

    @Override
    @SneakyThrows
    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) throws BeansException {
        Object[] advisors = allAdvisors.stream().filter(advisor -> advisor.getPointCut().matches(beanClass))
                .toArray();
        if (advisors.length == 0) {
            return null;
        }
        Object bean = beanClass.newInstance();
        return defaultProxyEnhancer.proxy(bean, beanClass, advisors);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
