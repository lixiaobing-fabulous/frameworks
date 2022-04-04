package com.lxb.cache;


import static java.util.ServiceLoader.load;

import java.net.URI;

import com.lxb.cache.cache.Cache;
import com.lxb.cache.cachemanager.CacheManager;
import com.lxb.cache.config.PropertiesCacheConfiguration;
import com.lxb.cache.provider.CachingProvider;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public final class Caching {
    public static CachingProvider getCachingProvider() {
        return load(CachingProvider.class, Caching.class.getClassLoader()).iterator().next();
    }

    public static void main(String[] args) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("in-memory://localhost/"), null);
        Cache<String, Integer> cache = cacheManager.createCache("simpleCache",
                new PropertiesCacheConfiguration<String, Integer>(cachingProvider.getDefaultProperties()));
        System.out.println(cache);
        String key = "key";
        Integer value1 = 1;
        cache.put("key", value1);

        // update
        value1 = 2;
        cache.put("key", value1);

        Integer value2 = cache.get(key);
        System.out.println(value2);
    }

}
