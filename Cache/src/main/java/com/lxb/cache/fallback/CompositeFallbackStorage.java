package com.lxb.cache.fallback;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.lxb.cache.cache.Cache.Entry;
import com.lxb.cache.cache.ExpirableEntry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public class CompositeFallbackStorage<K, V> extends BaseCacheFallbackStorage<K, V> {
    private final ConcurrentMap<ClassLoader, List<CacheFallbackStorage>> fallbackStoragesCache =
            new ConcurrentHashMap<>();

    private final List<CacheFallbackStorage> fallbackStorages;

    public CompositeFallbackStorage(ClassLoader classLoader) {
        super(Integer.MIN_VALUE);
        this.fallbackStorages = fallbackStoragesCache
                .computeIfAbsent(classLoader, this::loadFallbackStorages);
    }


    private List<CacheFallbackStorage> loadFallbackStorages(ClassLoader classLoader) {
        return stream(ServiceLoader.load(CacheFallbackStorage.class, classLoader).spliterator(), false)
                .sorted(PRIORITY_COMPARATOR)
                .collect(toList());
    }

    @Override
    public V load(K key) {
        for (CacheFallbackStorage<K, V> fallbackStorage : fallbackStorages) {
            V value = fallbackStorage.load(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void write(Entry<? extends K, ? extends V> entry) {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    @Override
    public void delete(Object key) {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    @Override
    public void destroy() {
        fallbackStorages.forEach(CacheFallbackStorage::destroy);
    }

    public static void main(String[] args) {
        CompositeFallbackStorage instance = new CompositeFallbackStorage(Thread.currentThread().getContextClassLoader());

        instance.writeAll(asList(ExpirableEntry.of("a", 1), ExpirableEntry.of("b", 2), ExpirableEntry.of("c", 3)));

        Map map = instance.loadAll(asList("a", "b", "c"));
        System.out.println(map);
    }
}
