package com.lxb.cache.config;

import static com.lxb.converter.Converter.convertIfPossible;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public interface PropertyConfiguration<K, V> extends Configuration<K, V> {


    String CACHE_PROPERTY_PREFIX = "lxb.cache.Cache.";
    String CACHE_KEY_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "key-type";
    String CACHE_VALUE_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "value-type";
    String STORE_BY_VALUE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "store-by-value";
    String READ_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "read-through";
    String WRITE_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "write-through";
    String STATISTICS_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "statistics-enabled";
    String MANAGEMENT_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "management-enabled";
    String ENTRY_LISTENER_CONFIGURATIONS_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "entry-listener-configurations";
    String CACHE_LOADER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "loader.factory";
    String CACHE_WRITER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "writer.factory";
    String EXPIRY_POLICY_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "expiry-policy.factory";

    String getProperty(String propertyName);

    default String getProperty(String propertyName, String defaultValue) {
        String propertyValue = getProperty(propertyName);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    default <T> T getProperty(String propertyName, Class<T> propertyType) {
        String propertyValue = getProperty(propertyName);
        return convertIfPossible(propertyValue, propertyType);
    }

    default <T> T getProperty(String propertyName, Class<T> propertyType, T defaultValue) {
        T propertyValue = getProperty(propertyName, propertyType);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    @Override
    default Class getKeyType() {
        return getProperty(CACHE_KEY_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }

    @Override
    default Class getValueType() {
        return getProperty(CACHE_VALUE_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }


    @Override
    default boolean isReadThrough() {
        return getProperty(READ_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isWriteThrough() {
        return getProperty(WRITE_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isStatisticsEnabled() {
        return getProperty(STATISTICS_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isManagementEnabled() {
        return getProperty(MANAGEMENT_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

}
