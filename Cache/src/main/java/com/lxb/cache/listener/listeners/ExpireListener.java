package com.lxb.cache.listener.listeners;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public interface ExpireListener<K, V> extends EventListener {
    void onExpire(Iterable<CacheEvent<K, V>> cacheEvents);
}
