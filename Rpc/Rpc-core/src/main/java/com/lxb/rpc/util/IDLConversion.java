package com.lxb.rpc.util;


/**
 * 接口描述语言参数转换接口
 */
public interface IDLConversion {

    /**
     * 把字段转换成参数数组
     *
     * @return 方法参数
     */
    Object[] toArgs();

    /**
     * 根据参数设置字段
     *
     * @param args 参数
     */
    void toFields(Object[] args);
}
