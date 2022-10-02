package com.lxb.rpc.apm.metric;



import com.lxb.rpc.util.MilliPeriod;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能窗口
 *
 * @date 2019年2月19日 下午2:21:42
 */
public interface TPWindow extends Metric, Snapshot<TPMetric> {

    /**
     * 请求成功
     *
     * @param timeMillis 耗费的时间，单位毫秒
     */
    void success(int timeMillis);

    /**
     * 成功请求一次
     *
     * @param timeMillis 耗费的时间，单位毫秒
     * @param records    记录数
     * @param dataSize   数据大小
     */
    void success(int timeMillis, int records, long dataSize);

    /**
     * 请求失败
     */
    void failure();

    /**
     * 重置请求失败数
     */
    void resetSuccessiveFailures();

    /**
     * 判断当前是否有记录的请求
     */
    boolean hasRequest();

    /**
     * 并发请求数
     *
     * @return 并发请求数
     */
    AtomicLong actives();

    /**
     * 待分发流量
     *
     * @return 待分发流量
     */
    AtomicLong distribution();

    /**
     * 窗口时间，单位毫秒
     *
     * @return 窗口时间，单位毫秒
     */
    long getWindowTime();

    /**
     * 获取熔断时间
     *
     * @return
     */
    MilliPeriod getBrokenPeriod();

    /**
     * 熔断
     *
     * @param duration   熔断时间（毫秒）
     * @param decubation 恢复期（毫秒）
     */
    void broken(long duration, long decubation);

    /**
     * 虚弱，进入恢复阶段，在指定时间短里面平滑恢复权重
     *
     * @param period   原子并发
     * @param duration 虚弱时间
     */
    void weak(MilliPeriod period, long duration);

    /**
     * 获取虚弱时间
     *
     * @return 虚弱时间
     */
    MilliPeriod getWeakPeriod();

}
