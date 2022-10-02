package com.lxb.rpc.apm.metric;


/**
 * 上一个周期的性能数据
 *
 * @date 2019年2月19日 下午2:29:11
 */
public interface TPSnapshot extends Metric {

    /**
     * 请求数
     *
     * @return
     */
    long getRequests();

    /**
     * 成功的请求数
     *
     * @return
     */
    long getSuccesses();

    /**
     * 失败的请求数
     *
     * @return
     */
    long getFailures();

    /**
     * 成功率
     *
     * @return
     */
    double getAvailability();

    /**
     * 记录数，一次请求可能多条数据
     *
     * @return
     */
    long getRecords();

    /**
     * 数据大小
     *
     * @return
     */
    long getDataSize();

    /**
     * 成功请求花费的实际
     *
     * @return
     */
    int getElapsedTime();

    /**
     * 最大时间
     *
     * @return
     */
    int getMax();

    /**
     * 最小时间
     *
     * @return
     */
    int getMin();

    /**
     * 平均时间
     *
     * @return
     */
    int getAvg();

    /**
     * TP40
     *
     * @return
     */
    int getTp30();

    /**
     * TP50
     *
     * @return
     */
    int getTp50();

    /**
     * TP90
     *
     * @return
     */
    int getTp90();

    /**
     * TP99
     *
     * @return
     */
    int getTp99();

    /**
     * TP999
     *
     * @return
     */
    int getTp999();

}
