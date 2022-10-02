package com.lxb.rpc.apm.metric;


/**
 * 快照
 */
public interface Snapshot<T extends Metric> {

    /**
     * 创建指标快照
     */
    void snapshot();

    /**
     * 获取最近周期创建的指标快照
     *
     * @return 指标快照
     */
    T getSnapshot();

    /**
     * 判断是否过期
     *
     * @return 过期标识
     */
    boolean isExpired();

    /**
     * 设置上次快照时间，单位毫秒
     *
     * @param timeMillis 上次快照时间，单位毫秒
     */
    void setLastSnapshotTime(long timeMillis);

}
