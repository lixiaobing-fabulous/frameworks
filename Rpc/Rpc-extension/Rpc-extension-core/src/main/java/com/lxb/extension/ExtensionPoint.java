package com.lxb.extension;


import java.util.List;
import java.util.function.Predicate;

/**
 * 扩展点接口
 *
 * @param <T>
 * @param <M>
 */
public interface ExtensionPoint<T, M> {

    /**
     * 按照名称获取指定扩展实现，字符串名称后面加上"@供应商"来优先获取指定供应商的扩展
     *
     * @param name 扩展名称
     * @return 扩展实现
     */
    T get(M name);

    /**
     * 按照指定名称或候选名称获取扩展
     *
     * @param name   扩展名称
     * @param option 候选名称
     * @return 扩展实现
     */
    T get(M name, M option);

    /**
     * 按照指定名称获取扩展，如果股存在则返回优先级最高的扩展
     *
     * @param name 扩展名称
     * @return 扩展实现
     */
    T getOrDefault(M name);

    /**
     * 遍历名称，找到第一个扩展
     *
     * @param names 名称数组
     * @return 匹配的第一个扩展
     */
    T get(M[] names);

    /**
     * 选择一个实现
     *
     * @return 扩展实现
     */
    T get();

    /**
     * 数量
     *
     * @return 数量
     */
    int size();

    /**
     * 获取扩展实现列表
     *
     * @return 扩展实现迭代
     */
    Iterable<T> extensions();

    /**
     * 获取扩展实现列表
     *
     * @param predicate 选择器
     * @return 扩展实现迭代
     */
    Iterable<T> extensions(Predicate<T> predicate);

    /**
     * 反序获取扩展实现列表
     *
     * @return 反序的扩展实现迭代
     */
    Iterable<T> reverse();

    /**
     * 扩展元数据迭代
     *
     * @return 扩展元数据迭代
     */
    Iterable<ExtensionMeta<T, M>> metas();

    /**
     * 扩展元数据迭代
     *
     * @param name 名称
     * @return 扩展元数据迭代
     */
    Iterable<ExtensionMeta<T, M>> metas(M name);

    /**
     * 获取扩展元数据，字符串名称后面加上"@供应商"来优先获取指定供应商的扩展元数据
     *
     * @param name 扩展实现名称
     * @return 扩展元数据
     */
    ExtensionMeta<T, M> meta(M name);

    /**
     * 扩展点名称
     *
     * @return 扩展点名称对象
     */
    Name<T, String> getName();

    /**
     * 获取扩展实现名称
     *
     * @return 扩展实现名称集合
     */
    List<M> names();

    /**
     * 获取可用的扩展实现名称
     *
     * @param names 名称
     * @return 可用的扩展实现名称集合
     */
    List<M> available(List<M> names);
}
