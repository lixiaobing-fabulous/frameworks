package com.lxb.rpc.util;


import java.lang.reflect.Constructor;

/**
 * 泛型构造函数
 */
public class GenericConstructor extends GenericExecutable<Constructor> {

    /**
     * 构造函数
     *
     * @param method     构造函数
     * @param parameters 泛型参数
     * @param exceptions 泛型异常
     */
    public GenericConstructor(final Constructor method, final GenericType[] parameters, final GenericType[] exceptions) {
        super(method, parameters, exceptions);
    }
}
