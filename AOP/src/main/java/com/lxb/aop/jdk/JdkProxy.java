package com.lxb.aop.jdk;


import static com.lxb.aop.util.ClassLoaderUtil.getClassLoader;
import static java.lang.reflect.Proxy.newProxyInstance;

import com.lxb.aop.AopProxy;

public class JdkProxy implements AopProxy {

    public <T> T proxy(T source, Class<? super T> clazz, Object... interceptors) {
        return (T) newProxyInstance(getClassLoader(clazz), new Class[]{clazz}, new JdkInvocationHandler(source, interceptors));
    }
}
