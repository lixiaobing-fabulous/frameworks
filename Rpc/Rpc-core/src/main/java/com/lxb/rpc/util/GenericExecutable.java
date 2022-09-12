package com.lxb.rpc.util;


import java.lang.reflect.Executable;
import java.lang.reflect.Type;

/**
 * 泛型方法
 */
public abstract class GenericExecutable<T extends Executable> {

    /**
     * 方法
     */
    protected T method;

    /**
     * 参数泛型
     */
    protected GenericType[] parameters;
    /**
     * 异常
     */
    protected GenericType[] exceptions;
    /**
     * 参数泛型
     */
    protected Type[] genericTypes;
    /**
     * 参数类型
     */
    protected Class<?>[] types;

    /**
     * 构造函数
     *
     * @param method     方法
     * @param parameters 泛型参数
     * @param exceptions 泛型异常
     */
    public GenericExecutable(final T method, final GenericType[] parameters, final GenericType[] exceptions) {
        this.method = method;
        this.parameters = parameters;
        this.exceptions = exceptions;
        this.genericTypes = new Type[parameters.length];
        this.types = new Class<?>[parameters.length];
        for (int i = 0; i < genericTypes.length; i++) {
            genericTypes[i] = parameters[i].getGenericType();
            types[i] = parameters[i].getType();
        }
    }

    public T getMethod() {
        return method;
    }

    public GenericType[] getParameters() {
        return parameters;
    }

    public GenericType[] getExceptions() {
        return exceptions;
    }

    public Type[] getGenericTypes() {
        return genericTypes;
    }

    public Class<?>[] getTypes() {
        return types;
    }
}
