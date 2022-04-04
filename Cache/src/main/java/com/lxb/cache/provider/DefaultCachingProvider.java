package com.lxb.cache.provider;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import com.lxb.cache.cachemanager.AbstractCacheManager;
import com.lxb.cache.cachemanager.CacheManager;

import lombok.SneakyThrows;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-21
 */
public class DefaultCachingProvider implements CachingProvider {
    private Properties defaultProperties;
    private ConcurrentMap<String, CacheManager> cacheManagers = new ConcurrentHashMap<>();
    public static final String DEFAULT_ENCODING = System.getProperty("file.encoding", "UTF-8");
    private URI defaultURI;

    public static final String DEFAULT_PROPERTIES_PRIORITY_PROPERTY_NAME =
            "lxb.cache.spi.CachingProvider.default-properties.priority";
    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME_PROPERTY_NAME =
            "lxb.cache.spi.CachingProvider.default-properties";
    public static final String DEFAULT_URI_PROPERTY_NAME = "lxb.cache.spi.CachingProvider.default-uri";
    public static final String DEFAULT_URI_DEFAULT_PROPERTY_VALUE = "in-memory://localhost/";

    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME = System.getProperty(
            DEFAULT_PROPERTIES_RESOURCE_NAME_PROPERTY_NAME,
            "META-INF/default-caching-provider.properties");
    public static final String CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX = "lxb.cache.CacheManager.mappings.";

    private ConcurrentMap<String, CacheManager> cacheManagersRepository = new ConcurrentHashMap<>();


    @Override
    public Properties getDefaultProperties() {
        if (this.defaultProperties == null) {
            this.defaultProperties = loadDefaultProperties();
        }
        return defaultProperties;
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (Objects.nonNull(contextClassLoader)) {
            return contextClassLoader;
        }
        return this.getClass().getClassLoader();
    }

    @Override
    public URI getDefaultURI() {
        if (defaultURI == null) {
            String defaultURIValue =
                    getDefaultProperties().getProperty(DEFAULT_URI_PROPERTY_NAME, DEFAULT_URI_DEFAULT_PROPERTY_VALUE);
            defaultURI = URI.create(defaultURIValue);
        }
        return defaultURI;
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return getCacheManager(uri, classLoader, getDefaultProperties());
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        URI actualURI = getOrDefault(uri, this::getDefaultURI);
        ClassLoader actualClassLoader = getOrDefault(classLoader, this::getDefaultClassLoader);
        Properties actualProperties = new Properties(getDefaultProperties());
        if (properties != null && !properties.isEmpty()) {
            actualProperties.putAll(properties);
        }

        String key = generateCacheManagerKey(actualURI, actualClassLoader, actualProperties);

        return cacheManagersRepository.computeIfAbsent(key, k ->
                newCacheManager(actualURI, actualClassLoader, actualProperties));
    }

    private String generateCacheManagerKey(URI uri, ClassLoader classLoader, Properties properties) {
        StringBuilder keyBuilder = new StringBuilder(uri.toASCIIString())
                .append("-").append(classLoader)
                .append("-").append(properties);
        return keyBuilder.toString();
    }

    @SneakyThrows
    private CacheManager newCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        CacheManager cacheManager = null;
        Class<? extends AbstractCacheManager> cacheManagerClass =
                getCacheManagerClass(uri, classLoader, properties);
        Class[] parameterTypes =
                new Class[] {CachingProvider.class, URI.class, ClassLoader.class, Properties.class};
        Constructor<? extends AbstractCacheManager> constructor = cacheManagerClass.getConstructor(parameterTypes);
        cacheManager = constructor.newInstance(this, uri, classLoader, properties);
        return cacheManager;
    }

    private Class<? extends AbstractCacheManager> getCacheManagerClass(URI uri, ClassLoader classLoader,
                                                                       Properties properties)
            throws ClassNotFoundException, ClassCastException {

        String className = getCacheManagerClassName(uri, properties);
        Class<? extends AbstractCacheManager> cacheManagerImplClass = null;
        Class<?> cacheManagerClass = classLoader.loadClass(className);
        if (!AbstractCacheManager.class.isAssignableFrom(cacheManagerClass)) {
            throw new ClassCastException(format("The implementation class of %s must extend %s",
                    CacheManager.class.getName(), AbstractCacheManager.class.getName()));
        }

        cacheManagerImplClass = (Class<? extends AbstractCacheManager>) cacheManagerClass;

        return cacheManagerImplClass;
    }

    private String getCacheManagerClassName(URI uri, Properties properties) {
        String propertyName = getCacheManagerClassNamePropertyName(uri);
        String className = properties.getProperty(propertyName);
        if (className == null) {
            throw new IllegalStateException(
                    format("The implementation class name of %s that is the value of property '%s' "
                                    + "must be configured in the Properties[%s]", CacheManager.class.getName(),
                            propertyName,
                            properties));
        }
        return className;
    }

    private static String getCacheManagerClassNamePropertyName(URI uri) {
        String scheme = uri.getScheme();
        return CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX + scheme;
    }


    private <T> T getOrDefault(T value, Supplier<T> defaultValue) {
        return value == null ? defaultValue.get() : value;
    }


    @Override
    public void close() throws IOException {
        for (CacheManager cacheManager : cacheManagers.values()) {
            cacheManager.close();
        }
    }

    @SneakyThrows
    private Properties loadDefaultProperties() {
        ClassLoader classLoader = getDefaultClassLoader();
        Enumeration<URL> resources = classLoader.getResources(DEFAULT_PROPERTIES_RESOURCE_NAME);
        List<Properties> propertiesList = new LinkedList<>();

        //类路径读取配置
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Properties properties = new Properties();
            try (InputStream inputStream = url.openStream()) {
                properties.load(inputStream);
                propertiesList.add(properties);
            }
        }

        // 优先级排序
        propertiesList.sort((o1, o2) -> {
            Integer p1 =
                    Integer.decode(o1.getProperty(DEFAULT_PROPERTIES_PRIORITY_PROPERTY_NAME, "0x7fffffff"));
            Integer p2 = Integer.decode(o2.getProperty(DEFAULT_PROPERTIES_PRIORITY_PROPERTY_NAME, "0x7fffffff"));
            return Integer.compare(p1, p2);
        });

        // 优先级覆盖字段
        Properties result = new Properties();
        for (Properties properties : propertiesList) {
            for (String propertyName : properties.stringPropertyNames()) {
                result.computeIfAbsent(propertyName, properties::get);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        DefaultCachingProvider defaultCachingProvider = new DefaultCachingProvider();
        Properties defaultProperties = defaultCachingProvider.getDefaultProperties();
        System.out.println(defaultProperties);
    }

}
