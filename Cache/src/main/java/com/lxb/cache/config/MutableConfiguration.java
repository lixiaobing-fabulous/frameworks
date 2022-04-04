package com.lxb.cache.config;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class MutableConfiguration<K, V> implements Configuration<K, V> {
    protected boolean isReadThrough;
    protected boolean isWriteThrough;
    protected boolean isStatisticsEnabled;
    protected boolean isStoreByValue;
    protected boolean isManagementEnabled;
    protected Class<K> keyType;
    protected Class<V> valueType;

    public MutableConfiguration() {
        this.keyType = (Class<K>) Object.class;
        this.valueType = (Class<V>) Object.class;
        this.isReadThrough = false;
        this.isWriteThrough = false;
        this.isStatisticsEnabled = false;
        this.isStoreByValue = true;
        this.isManagementEnabled = false;
    }

    public MutableConfiguration(Configuration configuration) {
        this.isReadThrough = configuration.isReadThrough();
        this.isWriteThrough = configuration.isWriteThrough();
        this.isStatisticsEnabled = configuration.isStatisticsEnabled();
        this.isManagementEnabled = configuration.isManagementEnabled();
        this.keyType = configuration.getKeyType();
        this.valueType = configuration.getValueType();
    }

    public MutableConfiguration<K, V> setTypes(Class<K> key, Class<V> value) {
        if (key != null && value != null) {
            this.keyType = key;
            this.valueType = value;
            return this;
        } else {
            throw new NullPointerException("keyType and/or valueType can't be null");
        }
    }

    @Override
    public boolean isReadThrough() {
        return false;
    }

    @Override
    public boolean isWriteThrough() {
        return false;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return false;
    }

    @Override
    public boolean isManagementEnabled() {
        return false;
    }

    @Override
    public Class<K> getKeyType() {
        return null;
    }

    @Override
    public Class<V> getValueType() {
        return null;
    }
}
