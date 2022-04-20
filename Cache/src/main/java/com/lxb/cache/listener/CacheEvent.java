package com.lxb.cache.listener;

import com.lxb.cache.cache.Cache;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-14
 */
public interface CacheEvent<K, V> {
    K getKey();

    Cache<K, V> getSource();

    V getValue();

    V getOldValue();

    boolean isOldValueAvailable();

    EventType getEventType();

}
