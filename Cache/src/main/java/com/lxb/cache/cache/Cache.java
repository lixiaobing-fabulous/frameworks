package com.lxb.cache.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface Cache<K, V> {
    boolean containsKey(K key);

    V get(K key);

    Map<K, V> getAll(Set<? extends K> keys);

    V getAndPut(K key, V value);

    V getAndReplace(K key, V value);

    V getAndRemove(K key);

    boolean remove(K key);

    boolean remove(K key, V oldValue);

    void removeAll();

    void removeAll(Set<? extends K> keys);

    Iterator<Entry<K, V>> iterator();

    void put(K key, V value);

    void putAll(Map<? extends K, ? extends V> map);

    boolean putIfAbsent(K key, V value);

    boolean replace(K key, V value);

    boolean replace(K key, V oldValue, V newValue);

    void clear();

    void close();

    boolean isClosed();

    interface Entry<K, V> {
        K getKey();

        V getValue();
    }
}
