package com.lxb.cache.provider;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;

import com.lxb.cache.cachemanager.CacheManager;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-21
 */
public interface CachingProvider extends Closeable {

    Properties getDefaultProperties();

    ClassLoader getDefaultClassLoader();

    URI getDefaultURI();

    CacheManager getCacheManager(URI uri, ClassLoader classLoader);

    CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties);

}
