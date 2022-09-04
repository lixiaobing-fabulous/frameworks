package com.lxb.rpc.util;


import java.util.Iterator;
import java.util.function.Predicate;

/**
 * 类及超类迭代器
 */
public class SuperIterator implements Iterator<Class<?>> {
    /**
     * 遍历的类
     */
    protected Class<?> clazz;
    /**
     * 判断
     */
    protected Predicate<Class<?>> predicate;

    /**
     * 构造函数
     *
     * @param clazz
     */
    public SuperIterator(Class<?> clazz) {
        this(clazz, null);
    }

    /**
     * 构造函数
     *
     * @param clazz
     * @param predicate
     */
    public SuperIterator(Class<?> clazz, Predicate<Class<?>> predicate) {
        this.clazz = clazz;
        this.predicate = predicate == null ? o -> o.equals(Object.class) : predicate;
    }

    @Override
    public boolean hasNext() {
        return clazz != null && !predicate.test(clazz);
    }

    @Override
    public Class<?> next() {
        Class<?> result = clazz;
        clazz = clazz.getSuperclass();
        return result;
    }

    @Override
    public void remove() {

    }
}
