package com.lxb.rpc.cache.map;


import com.lxb.rpc.cache.AbstractCache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheObject;
import com.lxb.rpc.util.SystemClock;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Map的缓存，缓存空值
 */
public class MapCache<K, V> extends AbstractCache<K, V> {
    /**
     * 名称
     */
    protected String name;

    /**
     * 缓存
     */
    protected Map<K, MapCacheObject<V>> caches;

    /**
     * 构造函数
     *
     * @param name   名称
     * @param config 配置
     */
    public MapCache(String name, CacheConfig<K, V> config) {
        this.name = name;
        this.config = config == null ? new CacheConfig<>() : config;
        this.caches = createCaches();
    }

    /**
     * 创建缓存
     *
     * @return
     */
    protected Map<K, MapCacheObject<V>> createCaches() {
        return config.getCapacity() <= 0 ? new ConcurrentHashMap<>(1024) : Collections.synchronizedMap(new LRUHashMap<>(config.getCapacity()));
    }

    @Override
    protected CompletableFuture<Void> doPut(final K key, final V value) {
        long expireTime = config.getExpireAfterWrite() > 0 ? SystemClock.now() + config.getExpireAfterWrite() : -1;
        caches.put(key, new MapCacheObject<>(value, expireTime));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected CompletableFuture<CacheObject<V>> doGet(final K key) {
        //获取缓存
        MapCacheObject<V> cache = caches.get(key);
        if (cache != null && cache.isExpire()) {
            //过期了
            if (cache.getCounter().compareAndSet(0, 1)) {
                //让一个进行操作
                caches.remove(key);
            }
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(cache);
    }

    @Override
    protected CompletableFuture<Void> doRemove(final K key) {
        caches.remove(key);
        return CompletableFuture.completedFuture(null);
    }

}
