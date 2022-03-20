package com.lxb.cache.fallback;

import java.util.Collection;

import com.lxb.cache.cache.Cache.Entry;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface CacheWriter<K, V> {

    void write(Entry<? extends K, ? extends V> entry);

    void writeAll(Collection<Entry<? extends K, ? extends V>> entrys);

    void delete(Object key);

    void deleteAll(Collection<?> keys);

}
