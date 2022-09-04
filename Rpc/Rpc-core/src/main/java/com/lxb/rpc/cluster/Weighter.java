package com.lxb.rpc.cluster;


/**
 * 权重
 */
public interface Weighter {

    /**
     * 获取权重
     *
     * @return
     */
    int getWeight();
}
