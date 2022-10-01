package com.lxb.rpc.cache.map;


import com.lxb.rpc.cache.CacheObject;
import com.lxb.rpc.util.SystemClock;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存数据对象
 *
 * @param <V>
 */
public class MapCacheObject<V> extends CacheObject<V> {
    //过期时间，毫秒数
    protected long expireTime;
    //计数器
    protected AtomicLong counter = new AtomicLong(0);

    /**
     * 构造函数
     *
     * @param result
     */
    public MapCacheObject(V result) {
        super(result);
    }

    /**
     * 构造函数
     *
     * @param result
     * @param expireTime
     */
    public MapCacheObject(V result, long expireTime) {
        super(result);
        this.expireTime = expireTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public AtomicLong getCounter() {
        return counter;
    }

    /**
     * 是否过期
     *
     * @return
     */
    public boolean isExpire() {
        return expireTime > 0 && SystemClock.now() > expireTime;
    }

    /**
     * 是否过期
     *
     * @param now 当前毫秒数
     * @return
     */
    public boolean isExpire(long now) {
        return expireTime > 0 && now > expireTime;
    }
}
