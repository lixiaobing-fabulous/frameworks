package com.lxb.cache.jmx;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-06
 */
public interface CacheStatistics extends CacheStatisticsMXBean {

    CacheStatistics reset();

    CacheStatistics cacheHits();

    CacheStatistics cacheGets();

    CacheStatistics cachePuts();

    CacheStatistics cacheRemovals();

    CacheStatistics cacheEvictions();

    CacheStatistics cacheGetsTime(long costTime);

    CacheStatistics cachePutsTime(long costTime);

    CacheStatistics cacheRemovesTime(long costTime);

}
