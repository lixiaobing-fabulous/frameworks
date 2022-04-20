package com.lxb.cache.jmx;

import javax.management.MXBean;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-06
 */
@MXBean
public interface CacheStatisticsMXBean {
    void clear();

    long getCacheHits();

    float getCacheHitPercentage();

    long getCacheMisses();

    float getCacheMissPercentage();

    long getCacheGets();

    long getCachePuts();

    long getCacheRemovals();

    long getCacheEvictions();

    float getAverageGetTime();

    float getAveragePutTime();

    float getAverageRemoveTime();

}
