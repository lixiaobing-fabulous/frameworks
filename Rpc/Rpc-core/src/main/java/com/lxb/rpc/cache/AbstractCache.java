package com.lxb.rpc.cache;


import com.lxb.rpc.exception.CacheException;
import com.lxb.rpc.util.Futures;

import java.util.concurrent.CompletableFuture;

/**
 * 同步缓存抽象实现
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /**
     * 配置
     */
    protected CacheConfig<K, V> config;

    @Override
    public CompletableFuture<Void> put(final K key, final V value) {
        if (key == null) {
            return Futures.completeExceptionally(new NullPointerException("key can not be null."));
        }
        try {
            if (value != null || config.nullable) {
                //判断是否缓存空值
                return doPut(key, value);
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return Futures.completeExceptionally(new CacheException(e.getMessage(), e));
        }

    }

    /**
     * 同步修改
     *
     * @param key   键
     * @param value 值
     */
    protected abstract CompletableFuture<Void> doPut(K key, V value);

    @Override
    public CompletableFuture<CacheObject<V>> get(final K key) {
        if (key == null) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            return doGet(key);
        } catch (Exception e) {
            return Futures.completeExceptionally(new CacheException(e.getMessage(), e));
        }
    }

    /**
     * 同步获取
     *
     * @param key 键
     * @return 缓存对象
     */
    protected abstract CompletableFuture<CacheObject<V>> doGet(K key);

    @Override
    public CompletableFuture<Void> remove(final K key) {
        if (key == null) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            return doRemove(key);
        } catch (Exception e) {
            return Futures.completeExceptionally(new CacheException(e.getMessage(), e));
        }

    }

    /**
     * 同步删除
     *
     * @param key 键
     */
    protected abstract CompletableFuture<Void> doRemove(K key);
}
