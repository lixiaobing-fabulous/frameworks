package com.lxb.rpc.util;


import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 接口描述语言对应的方法信息
 */
public class IDLMethod {
    /**
     * 类型
     */
    protected Class<?> clazz;
    /**
     * 方法
     */
    protected Method method;
    /**
     * 类型提供者
     */
    protected Supplier<IDLMethodDesc> supplier;

    public IDLMethod(Class<?> clazz, Method method, Supplier<IDLMethodDesc> supplier) {
        this.clazz = clazz;
        this.method = method;
        this.supplier = supplier;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    public Supplier<IDLMethodDesc> getSupplier() {
        return supplier;
    }

    public IDLMethodDesc getType() {
        return supplier.get();
    }
}
