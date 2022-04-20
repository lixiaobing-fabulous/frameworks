package com.lxb.cache.listener.listeners;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public interface UpdateListener<K, V> extends EventListener {
    void onUpdate(Iterable<CacheEvent<K, V>> cacheEvents);
}
