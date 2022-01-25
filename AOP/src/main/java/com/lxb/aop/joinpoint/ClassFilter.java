package com.lxb.aop.joinpoint;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-24
 */
public interface ClassFilter {

    default boolean matches(Class<?> clazz) {
        return true;
    }
}
