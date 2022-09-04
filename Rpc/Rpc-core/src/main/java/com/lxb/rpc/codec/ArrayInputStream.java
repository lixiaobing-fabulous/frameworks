package com.lxb.rpc.codec;


/**
 * 能直接读取自己数组的流
 */
public interface ArrayInputStream {

    /**
     * 是否有数组
     *
     * @return
     */
    boolean hasArray();

    /**
     * 数组
     *
     * @return
     */
    byte[] array();

    /**
     * 数据开始偏移量
     *
     * @return
     */
    int arrayOffset();

    /**
     * 读取索引
     *
     * @return
     */
    int readerIndex();
}
