package com.lxb.rpc.codec.serialization;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型引用
 *
 * @param <T>
 */
public abstract class TypeReference<T> {

    protected final Type type;

    protected TypeReference() {
        this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

}
