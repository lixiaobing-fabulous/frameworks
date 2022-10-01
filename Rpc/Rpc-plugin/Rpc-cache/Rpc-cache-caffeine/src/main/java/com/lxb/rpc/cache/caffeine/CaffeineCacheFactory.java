package com.lxb.rpc.cache.caffeine;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cache.Cache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheFactory;
import com.lxb.rpc.cache.CacheObject;

import java.util.concurrent.TimeUnit;

import static com.lxb.rpc.cache.CacheFactory.CAFFEINE_ORDER;

/**
 * CaffeineCache实现
 */
@Extension(value = "caffeine", provider = "benmanes", order = CAFFEINE_ORDER)
@ConditionalOnClass("com.github.benmanes.caffeine.cache.Caffeine")
public class CaffeineCacheFactory implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> build(final String name, final CacheConfig<K, V> config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (config.getExpireAfterWrite() > 0) {
            builder.expireAfterWrite(config.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        builder.maximumSize(config.getCapacity() > 0 ? config.getCapacity() : Long.MAX_VALUE);
        com.github.benmanes.caffeine.cache.Cache<K, CacheObject<V>> cache = builder.build();
        return new CaffeineCache<>(cache, config);
    }
}
