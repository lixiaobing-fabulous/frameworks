package com.lxb.rpc.cache.map;


import com.lxb.extension.Extension;
import com.lxb.rpc.cache.Cache;
import com.lxb.rpc.cache.CacheConfig;
import com.lxb.rpc.cache.CacheFactory;

import static com.lxb.rpc.cache.CacheFactory.MAP_ORDER;

@Extension(value = "map", order = MAP_ORDER)
public class MapCacheFactory implements CacheFactory {

    @Override
    public <K, V> Cache<K, V> build(final String name, final CacheConfig<K, V> config) {
        return new MapCache<>(name, config);
    }
}
