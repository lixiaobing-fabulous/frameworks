package com.lxb.cache.fallback;

import java.util.Comparator;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface CacheFallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {
    Comparator<CacheFallbackStorage> PRIORITY_COMPARATOR = Comparator.comparingInt(CacheFallbackStorage::getPriority);

    int getPriority();


    void destroy();


}
