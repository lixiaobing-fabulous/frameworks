package com.lxb.cache.cachemanager;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;

import com.lxb.cache.cache.Cache;
import com.lxb.cache.config.Configuration;
import com.lxb.cache.provider.CachingProvider;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface CacheManager extends Closeable {
    <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType);

    <K, V> Cache<K, V> getCache(String cacheName);

    <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException;
    boolean isClosed();
    CachingProvider getCachingProvider();
    URI getURI();
    ClassLoader getClassLoader();
    Properties getProperties();

    Iterable<String> getCacheNames();

    void destroyCache(String cacheName) throws NullPointerException, IllegalStateException;
}
