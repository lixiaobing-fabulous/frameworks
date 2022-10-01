package com.lxb.rpc.cache;


import java.io.Serializable;

/**
 * 缓存对象
 *
 * @param <V>
 */
public class CacheObject<V> implements Serializable {
    /**
     * 结果值
     */
    protected V result;

    public CacheObject(V result) {
        this.result = result;
    }

    public V getResult() {
        return result;
    }
}
