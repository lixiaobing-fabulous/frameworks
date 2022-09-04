package com.lxb.extension;


/**
 * 扩展点选择器
 *
 * @param <T>
 * @param <M>
 * @param <C>
 * @param <K>
 */
public class ExtensionSelector<T, M, C, K> {
    /**
     * 扩展点
     */
    protected ExtensionPoint<T, M> extensionPoint;
    /**
     * 选择器
     */
    protected Selector<T, M, C, K> selector;

    /**
     * 构造函数
     *
     * @param extensionPoint 扩展点
     * @param selector 选择器
     */
    public ExtensionSelector(ExtensionPoint<T, M> extensionPoint, Selector<T, M, C, K> selector) {
        this.extensionPoint = extensionPoint;
        this.selector = selector;
    }

    /**
     * 选择
     *
     * @param condition 条件
     * @return
     */
    public K select(final C condition) {
        return selector.select(extensionPoint, condition);
    }

}
