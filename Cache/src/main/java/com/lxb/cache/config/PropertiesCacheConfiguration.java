package com.lxb.cache.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.lxb.cache.provider.CachingProvider;
import com.lxb.cache.provider.DefaultCachingProvider;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public class PropertiesCacheConfiguration<K, V> implements PropertyConfiguration<K, V> {

    private final Map<String, String> config;

    public PropertiesCacheConfiguration(Properties properties) {
        this.config = new HashMap<>(properties.size());
        for (String propertyName : properties.stringPropertyNames()) {
            config.put(propertyName, properties.getProperty(propertyName));
        }
    }

    @Override
    public String getProperty(String propertyName) {
        return config.get(propertyName);
    }

    public static void main(String[] args) {
        CachingProvider cachingProvider = new DefaultCachingProvider();
        PropertyConfiguration configuration = new PropertiesCacheConfiguration(cachingProvider.getDefaultProperties());
        System.out.println(configuration.getKeyType());
        System.out.println(configuration.getValueType());
        System.out.println(configuration.isManagementEnabled());
        System.out.println(configuration.isReadThrough());
        System.out.println(configuration.isWriteThrough());
        System.out.println(configuration.isStatisticsEnabled());
        System.out.println(configuration.isManagementEnabled());
    }

}
