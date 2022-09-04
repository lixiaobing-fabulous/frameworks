package com.lxb.rpc.proxy.jdk;


import com.lxb.extension.Extension;
import com.lxb.rpc.exception.ProxyException;
import com.lxb.rpc.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


/**
 * The type JDK proxy factory.
 */
@Extension("jdk")
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(final Class<T> clz, final InvocationHandler invoker, final ClassLoader classLoader) throws ProxyException {
        try {
            return (T) Proxy.newProxyInstance(classLoader, new Class[]{clz}, invoker);
        } catch (IllegalArgumentException e) {
            throw new ProxyException(e.getMessage(), e);
        }
    }
}
