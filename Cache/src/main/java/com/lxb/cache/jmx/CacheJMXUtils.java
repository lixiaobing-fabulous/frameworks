package com.lxb.cache.jmx;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.lxb.cache.cache.Cache;
import com.lxb.cache.config.Configuration;

import lombok.SneakyThrows;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-13
 */
public abstract class CacheJMXUtils {

    public static void registerMBeansIfRequired(Cache<?, ?> cache, CacheStatistics cacheStatistics) {
        Configuration configuration = cache.getConfiguration();
        if (configuration.isManagementEnabled()) {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            registerCacheMXBeanIfRequired(cache, configuration, mBeanServer);
        }
        if (configuration.isStatisticsEnabled()) {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            registerCacheStatisticsMXBeanIfRequired(cache, configuration, mBeanServer, cacheStatistics);
        }

    }

    private static void registerCacheStatisticsMXBeanIfRequired(Cache<?, ?> cache, Configuration configuration,
                                                                MBeanServer mBeanServer,
                                                                CacheStatistics cacheStatistics) {
        if (configuration.isStatisticsEnabled()) {
            ObjectName objectName = createObjectName(cache, "CacheStatistics");
            registerMBean(objectName, cacheStatistics, mBeanServer);
        }
    }

    private static void registerCacheMXBeanIfRequired(Cache<?, ?> cache, Configuration configuration,
                                                      MBeanServer mBeanServer) {
        ObjectName objectName = createObjectName(cache, "CacheConfiguration");
        registerMBean(objectName, new CacheConfigMXBeanAdaptor(configuration), mBeanServer);
    }

    @SneakyThrows
    private static void registerMBean(ObjectName objectName, Object object, MBeanServer mBeanServer) {
        if (!mBeanServer.isRegistered(objectName)) {
            mBeanServer.registerMBean(object, objectName);
        }
    }


    private static ObjectName createObjectName(Cache<?, ?> cache,
                                               String type) {
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", type);
        props.put("name", cache.getName());
        props.put("uri", getUri(cache));
        ObjectName objectName;
        try {
            objectName = new ObjectName("lxb.cache", props);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        return objectName;
    }

    private static String getUri(Cache<?, ?> cache) {
        URI uri = cache.getCacheManager().getURI();
        try {
            return URLEncoder.encode(uri.toASCIIString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


}
