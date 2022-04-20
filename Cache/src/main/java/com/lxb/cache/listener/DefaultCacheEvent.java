package com.lxb.cache.listener;

import com.lxb.cache.cache.Cache;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-14
 */
public abstract class DefaultCacheEvent<K, V> implements CacheEvent<K, V> {
    private final K key;
    private final V value;
    private final V oldValue;
    private final Cache<K, V> cache;

    public DefaultCacheEvent(K key, V value, V oldValue, Cache<K, V> cache) {
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
        this.cache = cache;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public Cache<K, V> getSource() {
        return cache;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V getOldValue() {
        return this.oldValue;
    }

}
