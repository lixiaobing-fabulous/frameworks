package com.lxb.extension;


/**
 * 选项
 *
 * @param <T>
 */
public class Option<T> {
    /**
     * 值
     */
    T value;

    /**
     * 构造函数
     */
    public Option() {
    }

    /**
     * 构造函数
     *
     * @param value
     */
    public Option(T value) {
        this.value = value;
    }

    /**
     * 获取值
     *
     * @return
     */
    public T get() {
        return getValue();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
