package com.lxb.rpc.context;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨机房访问优先访问的机房
 */
public class CircuitConfiguration {

    public static final CircuitConfiguration CIRCUIT = new CircuitConfiguration();

    /**
     * 结果缓存
     */
    protected volatile Map<String, List<String>> CIRCUITS = new HashMap<>();

    /**
     * 读取跨机房访问优先访问的机房
     *
     * @param dataCenter
     * @return 结果
     */
    public List<String> get(final String dataCenter) {
        return dataCenter == null ? null : CIRCUITS.get(dataCenter);
    }

    /**
     * 修改配置
     *
     * @param circuits
     */
    public synchronized void update(final Map<String, List<String>> circuits) {
        CIRCUITS = circuits == null ? new HashMap<>() : circuits;
    }

}
