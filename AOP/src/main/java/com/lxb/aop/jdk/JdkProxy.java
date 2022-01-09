package com.lxb.aop.jdk;


import com.lxb.aop.AopProxy;

import static com.lxb.aop.util.ClassLoaderUtil.getClassLoader;
import static java.lang.reflect.Proxy.newProxyInstance;

public class JdkProxy implements AopProxy {

    public <T> T proxy(T source, Class<? super T> clazz, Object... interceptors) {
        return (T) newProxyInstance(getClassLoader(clazz), new Class[]{clazz}, new JdkInvocationHandler(source, interceptors));
    }
}
