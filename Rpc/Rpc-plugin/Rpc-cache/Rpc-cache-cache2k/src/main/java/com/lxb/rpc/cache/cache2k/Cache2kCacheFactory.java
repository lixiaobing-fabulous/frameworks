package com.lxb.rpc.cache.cache2k;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cache.Cache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheFactory;
import com.lxb.rpc.cache.CacheObject;
import org.cache2k.Cache2kBuilder;

import java.util.concurrent.TimeUnit;

import static com.lxb.rpc.cache.CacheFactory.CACHE2K_ORDER;


/**
 * Cache2k实现
 */
@Extension(value = "cache2k", provider = "cache2k", order = CACHE2K_ORDER)
@ConditionalOnClass("org.cache2k.Cache2kBuilder")
public class Cache2kCacheFactory implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> build(final String name, final CacheConfig<K, V> config) {
        Cache2kBuilder<K, CacheObject<V>> builder = Cache2kBuilder.forUnknownTypes();
        if (config.getKeyClass() != null) {
            builder.keyType(config.getKeyClass());
        }
        builder.valueType(CacheObject.class);
        builder.permitNullValues(config.isNullable());
        builder.entryCapacity(config.getCapacity() > 0 ? config.getCapacity() : Long.MAX_VALUE);

        if (config.getExpireAfterWrite() > 0) {
            builder.expireAfterWrite(config.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        } else {
            builder.eternal(true);
        }

        return new Cache2kCache<>(builder.build(), config);
    }
}
