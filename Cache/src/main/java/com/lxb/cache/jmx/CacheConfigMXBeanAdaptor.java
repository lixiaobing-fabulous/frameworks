package com.lxb.cache.jmx;

import com.lxb.cache.config.Configuration;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-13
 */
public class CacheConfigMXBeanAdaptor implements CacheConfigMXBean {
    private Configuration configuration;

    public CacheConfigMXBeanAdaptor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }


    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }
}
