package com.lxb.rpc.util;


/**
 * 接口描述语言产生的类型描述
 */
public class IDLType {

    /**
     * 请求类型
     */
    protected final Class<?>     clazz;
    /**
     * 包装请求
     */
    protected final boolean      wrapper;
    /**
     * 转换函数
     */
    protected final IDLConverter conversion;

    /**
     * 构造函数
     *
     * @param clazz   类型
     * @param wrapper 包装类型标识
     */
    public IDLType(Class<?> clazz, boolean wrapper) {
        this.clazz = clazz;
        this.wrapper = wrapper;
        // TDOO
//        this.conversion = wrapper ? ClassUtils.getConversion(clazz) : null;
        this.conversion = null;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isWrapper() {
        return wrapper;
    }

    public IDLConverter getConversion() {
        return conversion;
    }
}
