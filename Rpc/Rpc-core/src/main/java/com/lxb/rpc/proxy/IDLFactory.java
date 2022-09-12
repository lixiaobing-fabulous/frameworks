package com.lxb.rpc.proxy;



import com.lxb.rpc.exception.ProxyException;
import com.lxb.rpc.util.IDLMethodDesc;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 接口描述语言方法工厂类，负责生成参数和返回值的类型
 */
@FunctionalInterface
public interface IDLFactory {

    int ORDER_JDK = 100;

    int ORDER_JAVASSIST = 200;

    int ORDER_BYTE_BUDDY = 300;

    /**
     * 动态生成参数和返回值的包装类，便于支持IDL类型的调用
     *
     * @param clz    类型
     * @param method 方法
     * @return
     * @throws ProxyException
     */
    default IDLMethodDesc build(Class<?> clz, Method method) throws ProxyException {
        return build(clz, method, null);
    }

    /**
     * 动态生成参数和返回值的包装类，便于支持IDL类型的调用
     *
     * @param clz    类型
     * @param method 方法
     * @param suffix 包装类后缀提供者
     * @return
     * @throws ProxyException
     */
    IDLMethodDesc build(Class<?> clz, Method method, Supplier<String> suffix) throws ProxyException;
}
