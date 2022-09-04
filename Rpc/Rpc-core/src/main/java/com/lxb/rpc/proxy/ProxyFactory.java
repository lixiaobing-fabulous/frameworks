package com.lxb.rpc.proxy;


import com.lxb.extension.Extensible;
import com.lxb.rpc.exception.ProxyException;

import java.lang.reflect.InvocationHandler;

import static com.lxb.rpc.util.ClassUtils.getCurrentClassLoader;


/**
 * The interface Proxy factory.
 */
@Extensible("proxy")
public interface ProxyFactory {

    /**
     * Gets proxy.
     *
     * @param <T>     the type parameter
     * @param clz     the clz
     * @param invoker the invoker
     * @return the proxy
     * @throws ProxyException
     */
    default <T> T getProxy(final Class<T> clz, final InvocationHandler invoker) throws ProxyException {
        return getProxy(clz, invoker, getCurrentClassLoader());
    }

    /**
     * Gets proxy.
     *
     * @param <T>         the type parameter
     * @param clz         the clz
     * @param invoker     the invoker
     * @param classLoader the class loader
     * @return the proxy
     * @throws ProxyException
     */
    <T> T getProxy(Class<T> clz, InvocationHandler invoker, ClassLoader classLoader) throws ProxyException;

}
