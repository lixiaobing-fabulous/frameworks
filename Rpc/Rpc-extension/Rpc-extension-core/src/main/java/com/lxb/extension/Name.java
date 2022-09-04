package com.lxb.extension;


/**
 * 扩展名称
 */
public class Name<T, M> {
    /**
     * 类型
     */
    private final Class<T> clazz;
    /**
     * 名称
     */
    private final M name;

    public Name(Class<T> clazz) {
        this(clazz, null);
    }

    public Name(Class<T> clazz, M name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public M getName() {
        return name;
    }

}
