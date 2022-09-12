package com.lxb.rpc.constants;



/**
 * 头部键信息
 */
public class Head {

    /**
     * 值
     */
    protected byte key;
    /**
     * 类型
     */
    protected Class<?> type;

    /**
     * 构造函数
     *
     * @param key  键
     * @param type 值类型
     */
    public Head(final byte key, final Class<?> type) {
        this.key = key;
        this.type = type;
    }

    public byte getKey() {
        return key;
    }

    public Class<?> getType() {
        return type;
    }

}
