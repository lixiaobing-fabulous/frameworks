package com.lxb.extension;


/**
 * 分类算法
 */
public interface Classify<T, M> {

    /**
     * 获取类型
     *
     * @param obj  扩展对象
     * @param name 扩展点元数据名称
     * @return 类型
     */
    M type(T obj, Name<T, String> name);

}
