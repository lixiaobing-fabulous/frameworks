package com.lxb.extension;


/**
 * 排序
 */
public interface Ordered {

    /**
     * 默认顺序
     */
    int ORDER = Short.MAX_VALUE;

    /**
     * 排序顺序，按照优先级升序排序
     *
     * @return
     */
    int order();
}
