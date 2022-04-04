package com.lxb.cache.inmem;

import java.net.URI;
import java.util.Properties;

import com.lxb.cache.cache.Cache;
import com.lxb.cache.cachemanager.AbstractCacheManager;
import com.lxb.cache.config.Configuration;
import com.lxb.cache.provider.CachingProvider;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class InMemoryCacheManager extends AbstractCacheManager {

    public InMemoryCacheManager(CachingProvider cachingProvider, URI uri,
                                ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }
    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new InMemoryCache<K, V>(this, cacheName, configuration);
    }


}
