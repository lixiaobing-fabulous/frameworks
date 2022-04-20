package com.lxb.cache.listener;

import java.util.List;

import com.lxb.cache.listener.listeners.CreateListener;
import com.lxb.cache.listener.listeners.ExpireListener;
import com.lxb.cache.listener.listeners.RemoveListener;
import com.lxb.cache.listener.listeners.UpdateListener;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public class ListenerPublisher<K, V> {
    private List<Object> listeners;

    void onEvent(Iterable<CacheEvent<K, V>> cacheEvents) {
        for (Object listener : listeners) {
            if (listener instanceof CreateListener) {
                CreateListener createListener = (CreateListener) listener;
                createListener.onCreate(cacheEvents);
            }
            if (listener instanceof UpdateListener) {
                UpdateListener updateListener = (UpdateListener) listener;
                updateListener.onUpdate(cacheEvents);
            }
            if (listener instanceof RemoveListener) {
                RemoveListener removeListener = (RemoveListener) listener;
                removeListener.onRemove(cacheEvents);
            }
            if (listener instanceof ExpireListener) {
                ExpireListener expireListener = (ExpireListener) listener;
                expireListener.onExpire(cacheEvents);
            }
        }

    }

}
