package com.lxb.rpc.cache.cache2k;


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
public class Cache2kCache<K, V> extends AbstractCache<K, V> {
    /**
     * 缓存
     */
    protected org.cache2k.Cache<K, CacheObject<V>> cache;

    /**
     * 构造函数
     *
     * @param cache  缓存
     * @param config 配置
     */
    public Cache2kCache(org.cache2k.Cache<K, CacheObject<V>> cache, CacheConfig<K, V> config) {
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
        return CompletableFuture.completedFuture(cache.get(key));
    }

    @Override
    protected CompletableFuture<Void> doRemove(final K key) {
        cache.remove(key);
        return CompletableFuture.completedFuture(null);
    }

}
