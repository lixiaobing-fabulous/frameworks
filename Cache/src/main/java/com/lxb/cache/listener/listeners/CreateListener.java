package com.lxb.cache.listener.listeners;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public interface CreateListener<K, V> extends EventListener {
    void onCreate(Iterable<CacheEvent<K, V>> cacheEvents);
}
