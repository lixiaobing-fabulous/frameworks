package com.lxb.cache.fallback;

import java.util.Comparator;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public interface CacheFallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {
    Comparator<CacheFallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    int getPriority();


    void destroy();

    class PriorityComparator implements Comparator<CacheFallbackStorage> {

        @Override
        public int compare(CacheFallbackStorage o1, CacheFallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }

}
