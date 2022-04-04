package com.lxb.cache.cache;

import static java.util.Objects.requireNonNull;

import com.lxb.cache.cache.Cache.Entry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-27
 */
public class ExpirableEntry<K, V> implements Entry<K, V> {
    private final K key;

    private V value;
    private long timestamp;

    public ExpirableEntry(K key, V value) {
        requireNonNull(key, "The key must not be null.");
        this.key = key;
        setValue(value);
        this.timestamp = Long.MAX_VALUE; // default
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpired() {
        return getExpiredTime() < 1;
    }

    public long getExpiredTime() {
        return getTimestamp() - System.currentTimeMillis();
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        requireNonNull(value, "The value must not be null.");
        this.value = value;
    }

    public static <K, V> ExpirableEntry<K, V> of(K key, V value) {
        return new ExpirableEntry(key, value);
    }


    public boolean isEternal() {
        return Long.MAX_VALUE == getTimestamp();
    }

}
