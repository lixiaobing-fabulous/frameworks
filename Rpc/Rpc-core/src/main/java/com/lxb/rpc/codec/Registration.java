package com.lxb.rpc.codec;


import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.exception.SerializerException;

import java.util.Collection;

/**
 * 类型感知
 */
public interface Registration {

    /**
     * 注册序列化类
     *
     * @param clazz
     * @throws Serialization
     */
    void register(Class clazz) throws SerializerException;

    /**
     * 注册序列化类
     *
     * @param clazzs
     * @throws SerializerException
     */
    default void register(final Collection<Class<?>> clazzs) throws SerializerException {
        if (clazzs != null) {
            for (Class clazz : clazzs) {
                register(clazz);
            }
        }
    }
}
