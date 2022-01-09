package com.lxb.aop;

public interface AopProxy {

    <T> T proxy(T source, Class<? super T> clazz, Object... interceptors);

}
