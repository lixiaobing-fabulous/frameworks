package com.lxb.rpc.cache.caffeine;


import com.lxb.rpc.cache.AbstractCache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheObject;

import java.util.concurrent.CompletableFuture;

/**
 * GuavaCache
 *
 * @param <K>
 * @param <V>
 */
public class CaffeineCache<K, V> extends AbstractCache<K, V> {

    /**
     * 缓存
     */
    protected com.github.benmanes.caffeine.cache.Cache<K, CacheObject<V>> cache;

    /**
     * 构造函数
     *
     * @param cache  缓存
     * @param config 配置
     */
    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, CacheObject<V>> cache, CacheConfig<K, V> config) {
        this.cache = cache;
        this.config = config == null ? new CacheConfig<>() : config;
    }

    @Override
    protected CompletableFuture<Void> doPut(final K key, final V value) {
        cache.put(key, new CacheObject<>(value));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected CompletableFuture<CacheObject<V>> doGet(final K key) {
        return CompletableFuture.completedFuture(cache.getIfPresent(key));
    }

    @Override
    protected CompletableFuture<Void> doRemove(final K key) {
        cache.invalidate(key);
        return CompletableFuture.completedFuture(null);
    }

}
