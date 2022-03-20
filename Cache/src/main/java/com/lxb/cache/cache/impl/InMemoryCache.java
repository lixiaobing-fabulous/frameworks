package com.lxb.cache.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.lxb.cache.cache.AbstractCache;
import com.lxb.cache.cache.ExpirableEntry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public class InMemoryCache<K, V> extends AbstractCache<K, V> {
    private final Map<K, ExpirableEntry<K, V>> cache = new HashMap<>();

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) {
        return cache.remove(key);
    }

    @Override
    protected boolean containsEntry(K key) {
        return cache.containsKey(key);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) {
        return cache.get(key);
    }

    @Override
    protected Set<K> keySet() {
        return cache.keySet();
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) {
        K key = entry.getKey();
        cache.put(key, entry);
    }

    @Override
    protected void clearEntries() {
        cache.clear();
    }
}
