package com.lxb.aop;

import org.springframework.stereotype.Component;

import com.lxb.aop.cglib.CglibProxy;
import com.lxb.aop.jdk.JdkProxy;

@Component
public class DefaultProxyEnhancer implements AopProxy {
    private AopProxy cglibProxy;
    private AopProxy jdkProxy;

    public DefaultProxyEnhancer() {
        this.cglibProxy = new CglibProxy();
        this.jdkProxy = new JdkProxy();
    }

    @Override
    public <T> T proxy(T source, Class<? super T> clazz, Object... interceptors) {
        assertType(clazz);
        if (clazz.isInterface()) {
            return jdkProxy.proxy(source, clazz, interceptors);
        }
        return cglibProxy.proxy(source, clazz, interceptors);
    }

    private <T> void assertType(Class<? super T> type) {
        if (type.isAnnotation() || type.isEnum() || type.isPrimitive() || type.isArray()) {
            throw new IllegalArgumentException("The type must be an interface or a class!");
        }
    }

}
