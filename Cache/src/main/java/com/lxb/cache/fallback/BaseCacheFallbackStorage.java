package com.lxb.cache.fallback;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lxb.cache.cache.Cache.Entry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public abstract class BaseCacheFallbackStorage<K, V> implements CacheFallbackStorage<K, V> {

    private final int priority;

    protected BaseCacheFallbackStorage(int priority) {
        this.priority = priority;
    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, load(key));
        }
        return map;
    }

    @Override
    public void writeAll(Collection<Entry<? extends K, ? extends V>> entries) {
        entries.forEach(this::write);
    }

    @Override
    public void deleteAll(Collection<?> keys) {
        keys.forEach(this::delete);
    }

    @Override
    public int getPriority() {
        return priority;
    }

}
