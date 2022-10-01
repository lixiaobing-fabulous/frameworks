package com.lxb.rpc.cache;


import com.lxb.extension.Extensible;

/**
 * 缓存提供者
 */
@Extensible("cacheFactory")
public interface CacheFactory {

    int CAFFEINE_ORDER = 100;

    int CACHE2K_ORDER = CAFFEINE_ORDER + 1;

    int GUAVA_ORDER = CACHE2K_ORDER + 1;

    int MAP_ORDER = Short.MAX_VALUE;

    /**
     * 根据名称获取缓存
     *
     * @param name   名称
     * @param config 缓存配置
     * @return
     */
    <K, V> Cache<K, V> build(String name, CacheConfig<K, V> config);
}
