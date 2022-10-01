package com.lxb.rpc.util;


import java.util.function.Function;

/**
 * 接口描述语言对应的类型转换器
 */
public class IDLConverter {
    /**
     * 参数转换成包装对象函数
     */
    protected final Function<Object[], Object> toWrapper;
    /**
     * 包装对象转换成参数函数
     */
    protected final Function<Object, Object[]> toParameter;

    public IDLConverter(final Function<Object[], Object> toWrapper,
                        final Function<Object, Object[]> toParameter) {
        this.toWrapper = toWrapper;
        this.toParameter = toParameter;
    }

    public Function<Object[], Object> getToWrapper() {
        return toWrapper;
    }

    public Function<Object, Object[]> getToParameter() {
        return toParameter;
    }
}
