package com.lxb.cache.config;

import java.io.Serializable;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface Configuration<K, V> extends Serializable {
    /**
     * 读穿透
     */
    boolean isReadThrough();

    /**
     * 写穿透
     */
    boolean isWriteThrough();

    /**
     * mbean暴露统计信息
     */
    boolean isStatisticsEnabled();

    /**
     * 配置暴露mxbean修改功能
     */
    boolean isManagementEnabled();


    /**
     * 缓存key序列化类型
     */
    Class<K> getKeyType();

    /**
     * 缓存value序列化类型
     */
    Class<V> getValueType();
}
