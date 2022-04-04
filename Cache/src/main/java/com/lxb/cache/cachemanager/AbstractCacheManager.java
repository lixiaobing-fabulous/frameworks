package com.lxb.cache.cachemanager;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import com.lxb.cache.cache.Cache;
import com.lxb.cache.config.Configuration;
import com.lxb.cache.config.MutableConfiguration;
import com.lxb.cache.config.PropertiesCacheConfiguration;
import com.lxb.cache.provider.CachingProvider;
import com.lxb.serialize.Deserializers;
import com.lxb.serialize.Serializers;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-28
 */
public abstract class AbstractCacheManager implements CacheManager {
    private ConcurrentMap<String, Cache> cacheRepository = new ConcurrentHashMap<>();
    private static final Consumer<Cache> CLOSE_CACHE_OPERATION = Cache::close;

    private final ClassLoader classLoader;
    private final Serializers serializers;
    private final Deserializers deserializers;
    private final CachingProvider cachingProvider;
    private volatile boolean closed;
    private final URI uri;
    private final Properties properties;
    private final Configuration cacheConfiguration;


    public AbstractCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader,
                                Properties properties) {
        this.cachingProvider = cachingProvider;
        this.uri = uri == null ? cachingProvider.getDefaultURI() : uri;
        this.properties = properties == null ? cachingProvider.getDefaultProperties() : properties;
        this.cacheConfiguration = new PropertiesCacheConfiguration(this.properties);
        this.classLoader = classLoader == null ? cachingProvider.getDefaultClassLoader() : classLoader;
        this.serializers = initSerializers(this.classLoader);
        this.deserializers = initDeserializers(this.classLoader);

    }

    @Override
    public final CachingProvider getCachingProvider() {
        return cachingProvider;
    }

    @Override
    public final URI getURI() {
        return uri;
    }

    @Override
    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public final Properties getProperties() {
        return properties;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        MutableConfiguration<K, V> kvMutableConfiguration =
                new MutableConfiguration<K, V>(cacheConfiguration).setTypes(keyType, valueType);
        return getOrCreateCache(cacheName, kvMutableConfiguration,
                false);
    }
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return getCache(cacheName, (Class<K>) cacheConfiguration.getKeyType(), (Class<V>) cacheConfiguration.getValueType());
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
            throws IllegalArgumentException {
        if (cacheRepository.containsKey(cacheName)) {
            throw new RuntimeException(format("The Cache whose name is '%s' is already existed, "
                    + "please try another name to create a new Cache.", cacheName));
        }
        // If a Cache with the specified name is unknown the CacheManager, one is created according to
        // the provided Configuration after which it becomes managed by the CacheManager.
        return getOrCreateCache(cacheName, configuration, true);
    }

    @Override
    public Iterable<String> getCacheNames() {
        assertNotClosed();
        return cacheRepository.keySet();
    }

    private void assertNotClosed() throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException(
                    "The CacheManager has been closed, current operation should not be invoked!");
        }
    }


    private <V, K, C extends Configuration<K, V>> Cache<K, V> getOrCreateCache(String cacheName, C configuration,
                                                                               boolean created) {
        assertNotClosed();

        Cache<K, V> cache;

        if (created) {
            cache = cacheRepository.computeIfAbsent(cacheName, n -> doCreateCache(cacheName, configuration));
        } else {
            cache = cacheRepository.get(cacheName);
            if (cache != null) {
                C currentConfiguration = (C) cache.getConfiguration(configuration.getClass());
                if (!currentConfiguration.getKeyType().isAssignableFrom(configuration.getKeyType())
                        || !configuration.getValueType().isAssignableFrom(configuration.getValueType())) {
                    String message = format("The specified key[%s] and/or value[%s] types are incompatible with "
                                    + "the configured cache[key : %s , value : %s]",
                            configuration.getKeyType(),
                            configuration.getValueType(),
                            currentConfiguration.getKeyType(),
                            configuration.getValueType());
                    throw new ClassCastException(message);
                }
            }
        }
        return cache;

    }

    protected abstract <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration);

    protected Serializers initSerializers(ClassLoader cl) {
        Serializers serializer = new Serializers(cl);
        serializer.loadSPI();
        return serializer;
    }

    protected Deserializers initDeserializers(ClassLoader cl) {
        Deserializers deserializer = new Deserializers(cl);
        deserializer.loadSPI();
        return deserializer;
    }

    public Serializers getSerializers() {
        return serializers;
    }

    public Deserializers getDeserializers() {
        return deserializers;
    }


    @Override
    public void destroyCache(String cacheName) throws NullPointerException, IllegalStateException {
        requireNonNull(cacheName, "The 'cacheName' argument must not be null.");
        assertNotClosed();
        Cache cache = cacheRepository.remove(cacheName);
        if (cache != null) {
            cache.clear();
            cache.close();
        }
    }


    public void close() throws IOException {
        if (isClosed()) {
            return;
        }
        iterateCaches(cacheRepository.values(), CLOSE_CACHE_OPERATION);

        doClose();

        //  At this point in time the CacheManager:

        closed = true;

    }

    private void iterateCaches(Collection<Cache> caches, Consumer<Cache>... cacheOperations) {
        for (Cache cache : caches) {
            for (Consumer<Cache> cacheOperation : cacheOperations) {
                try {
                    cacheOperation.accept(cache);
                } catch (Throwable e) {
                    // just log, ignore the exception propagation
                }
            }
        }

    }

    protected void doClose() {
    }

}
