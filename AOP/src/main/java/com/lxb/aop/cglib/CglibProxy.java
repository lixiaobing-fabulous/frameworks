package com.lxb.aop.cglib;

import com.lxb.aop.AopProxy;

import net.sf.cglib.proxy.Enhancer;


public class CglibProxy implements AopProxy {

    public <T> T proxy(T source, Class<? super T> clazz, Object... interceptors) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibMethodInterceptor(source, interceptors));
        return (T) enhancer.create();
    }

}
