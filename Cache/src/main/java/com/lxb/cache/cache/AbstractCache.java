package com.lxb.cache.cache;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.lxb.cache.config.Configuration;
import com.lxb.cache.fallback.CacheLoader;
import com.lxb.cache.fallback.CacheWriter;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    private volatile boolean closed = false;
    private Configuration<K, V> configuration;
    private CacheLoader<K, V> cacheLoader;
    private CacheWriter<K, V> cacheWriter;

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        return containsEntry(key);
    }

    @Override
    public V get(K key) {
        assertNotClosed();
        requireNonNull(key, "The key must not be null.");
        ExpirableEntry<K, V> entry = getEntry(key);
        if (Objects.isNull(entry)) {
            if (configuration.isReadThrough()) {
                return loadValue(key, true);
            }
        } else {
            return getValue(entry);
        }
        return null;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    public V getAndPut(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        put(key, value);
        return oldValue;
    }

    @Override
    public V getAndReplace(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }


    @Override
    public V getAndRemove(K key) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        remove(key);
        return oldValue;
    }

    @Override
    public boolean remove(K key) {
        assertNotClosed();
        requireKeyNotNull(key);
        boolean removed;
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = oldEntry != null;
        } finally {
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeAll() {
        removeAll(keySet());
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        for (Object key : keys.toArray()) {
            remove((K) key);
        }
    }


    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        List<Entry<K, V>> entries = new LinkedList<>();
        for (K key : keySet()) {
            V value = get(key);
            entries.add(ExpirableEntry.of(key, value));
        }
        return entries.iterator();
    }


    protected abstract ExpirableEntry<K, V> removeEntry(K key);

    @Override
    public void put(K key, V value) {
        assertNotClosed();
        Entry<K, V> entry = null;
        try {
            if (!containsEntry(key)) {
                entry = createAndPutEntry(key, value);
            } else {
                entry = updateEntry(key, value);
            }
        } finally {
            if (Objects.nonNull(entry) && configuration.isWriteThrough()) {
                getCacheWriter().write(entry);
            }

        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        requireValueNotNull(oldValue);
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void clear() {
        assertNotClosed();
        clearEntries();
    }

    protected abstract boolean containsEntry(K key);

    protected abstract ExpirableEntry<K, V> getEntry(K key);

    protected abstract Set<K> keySet();

    protected abstract void putEntry(ExpirableEntry<K, V> entry);

    protected abstract void clearEntries();

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }
        doClose();
        closed = true;
    }

    protected void doClose() {
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }

    private V loadValue(K key, boolean storedEntry) {
        V value = loadValue(key);
        if (storedEntry && value != null) {
            put(key, value);
        }
        return value;
    }

    private V loadValue(K key) {
        return getCacheLoader().load(key);
    }

    protected CacheLoader<K, V> getCacheLoader() {
        return this.cacheLoader;
    }

    private CacheWriter<K, V> getCacheWriter() {
        return this.cacheWriter;
    }

    private static <K, V> V getValue(Entry<K, V> entry) {
        return entry == null ? null : entry.getValue();
    }

    public static <K> void requireKeyNotNull(K key) {
        requireNonNull(key, "The key must not be null.");
    }

    public static <V> void requireValueNotNull(V value) {
        requireNonNull(value, "The value must not be null.");
    }

    private Entry<K, V> createAndPutEntry(K key, V value) {
        // Create Cache.Entry
        ExpirableEntry<K, V> newEntry = createEntry(key, value);
        putEntry(newEntry);
        return newEntry;
    }

    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    private Entry<K, V> updateEntry(K key, V value) {
        // Update Cache.Entry
        ExpirableEntry<K, V> oldEntry = getEntry(key);

        V oldValue = oldEntry.getValue();
        // Update the value
        oldEntry.setValue(value);
        // Rewrite oldEntry
        putEntry(oldEntry);

        return oldEntry;
    }

    private void deleteIfWriteThrough(K key) {
        if (configuration.isWriteThrough()) {
            getCacheWriter().delete(key);
        }
    }

}
