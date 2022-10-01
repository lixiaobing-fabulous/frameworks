package com.lxb.rpc.cache;


import java.util.concurrent.CompletableFuture;

/**
 * 缓存接口，都改成异步，便于支持本地的分布式缓存，如Ignite
 */
public interface Cache<K, V> {

    /**
     * 放入缓存
     *
     * @param key   键
     * @param value 缓存数据
     */
    CompletableFuture<Void> put(K key, V value);

    /**
     * 从缓存中获取
     *
     * @param key 键
     * @return
     */
    CompletableFuture<CacheObject<V>> get(K key);

    /**
     * 移除缓存
     *
     * @param key 键
     */
    CompletableFuture<Void> remove(final K key);

}
