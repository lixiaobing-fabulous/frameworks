package com.lxb.cache.fallback;

import java.util.List;

import com.lxb.cache.cache.Cache.Entry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public class CompositeFallbackStorage<K, V> extends BaseCacheFallbackStorage<K, V> {
    private List<CacheFallbackStorage<K, V>> fallbackStorages;

    protected CompositeFallbackStorage() {
        super(Integer.MIN_VALUE);
    }

    @Override
    public V load(K key) {
        for (CacheFallbackStorage<K, V> fallbackStorage : fallbackStorages) {
            V value = fallbackStorage.load(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void write(Entry<? extends K, ? extends V> entry) {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    @Override
    public void delete(Object key) {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    @Override
    public void destroy() {
        fallbackStorages.forEach(CacheFallbackStorage::destroy);
    }
}
