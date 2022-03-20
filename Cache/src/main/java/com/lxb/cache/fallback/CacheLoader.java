package com.lxb.cache.fallback;

import java.util.Map;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface CacheLoader<K, V> {
    V load(K key);

    Map<K, V> loadAll(Iterable<? extends K> keys);
}
