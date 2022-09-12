package com.lxb.rpc.cluster.discovery.event;


import com.lxb.rpc.event.UpdateEvent;

import java.util.Map;


/**
 * 配置变更事件，只支持全量
 */
public class ConfigEvent extends UpdateEvent<Map<String, String>> {

    /**
     * 构造函数
     *
     * @param source
     * @param target
     * @param version
     * @param datum
     */
    public ConfigEvent(final Object source, final Object target, final long version, final Map<String, String> datum) {
        super(source, target, UpdateType.FULL, version, datum);
    }

    /**
     * 数据是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return datum == null || datum.isEmpty();
    }

}
