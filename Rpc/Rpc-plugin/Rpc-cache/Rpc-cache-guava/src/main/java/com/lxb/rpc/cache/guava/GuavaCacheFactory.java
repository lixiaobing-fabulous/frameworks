package com.lxb.rpc.cache.guava;


import com.google.common.cache.CacheBuilder;
import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cache.Cache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheFactory;
import com.lxb.rpc.cache.CacheObject;

import java.util.concurrent.TimeUnit;

import static com.lxb.rpc.cache.CacheFactory.GUAVA_ORDER;

/**
 * GuavaCache实现
 */
@Extension(value = "guava", provider = "google", order = GUAVA_ORDER)
@ConditionalOnClass("com.google.common.cache.CacheBuilder")
public class GuavaCacheFactory implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> build(final String name, final CacheConfig<K, V> config) {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        if (config.getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(config.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        cacheBuilder.maximumSize(config.getCapacity() > 0 ? config.getCapacity() : Long.MAX_VALUE);
        com.google.common.cache.Cache<K, CacheObject<V>> cache = cacheBuilder.build();
        return new GuavaCache<>(cache, config);
    }
}
