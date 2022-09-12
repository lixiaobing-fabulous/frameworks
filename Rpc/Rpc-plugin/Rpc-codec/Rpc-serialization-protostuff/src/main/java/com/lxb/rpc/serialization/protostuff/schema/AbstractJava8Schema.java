package com.lxb.rpc.serialization.protostuff.schema;


import io.protostuff.Schema;

import java.lang.reflect.Field;

public abstract class AbstractJava8Schema<T> implements Schema<T> {

    protected Class<T> clazz;

    public AbstractJava8Schema(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean isInitialized(final T message) {
        return true;
    }

    @Override
    public String messageName() {
        return clazz.getSimpleName();
    }

    @Override
    public String messageFullName() {
        return clazz.getName();
    }

    @Override
    public Class<? super T> typeClass() {
        return clazz;
    }

    /**
     * 设置值
     *
     * @param field
     * @param target
     * @param value
     */
    protected static void setValue(final Field field, final Object target, final Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * 获取字段
     *
     * @param clazz
     * @param name
     * @return
     */
    protected static Field getWriteableField(final Class clazz, final String name) {
        try {
            Field result = clazz.getDeclaredField(name);
            result.setAccessible(true);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
