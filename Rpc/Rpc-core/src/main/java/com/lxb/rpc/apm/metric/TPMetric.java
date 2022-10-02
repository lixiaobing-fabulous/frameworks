package com.lxb.rpc.apm.metric;


/**
 * 一个周期的性能指标
 *
 * @date 2019年2月19日 下午4:05:06
 */
public interface TPMetric extends Metric {

    /**
     * 当前连续失败次数
     *
     * @return
     */
    long getSuccessiveFailures();

    /**
     * 当前并发数
     *
     * @return
     */
    long getActives();

    /**
     * 待分发流量
     *
     * @return
     */
    long getDistribution();

    /**
     * 是否熔断中
     *
     * @return
     */
    boolean isBroken();

    /**
     * 上一个周期的性能数据
     *
     * @return
     */
    TPSnapshot getSnapshot();

}
