package com.lxb.cache.jmx;

import javax.management.MXBean;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-13
 */
@MXBean
public interface CacheConfigMXBean {
    String getKeyType();

    String getValueType();

    boolean isReadThrough();

    boolean isWriteThrough();

    boolean isStatisticsEnabled();

    boolean isManagementEnabled();

}
