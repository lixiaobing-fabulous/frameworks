package com.lxb.rpc.transaction;

import com.lxb.extension.Extensible;

import java.lang.reflect.Method;

/**
 * 事务提供者
 */
@Extensible("transaction")
public interface TransactionFactory {

    /**
     * 构建事务选项
     *
     * @param clazz  类
     * @param method 方法
     * @return 事务选项
     */
    TransactionOption create(final Class<?> clazz, final Method method);
}
