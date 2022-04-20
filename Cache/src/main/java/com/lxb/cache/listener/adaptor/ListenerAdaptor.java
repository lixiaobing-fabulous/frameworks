package com.lxb.cache.listener.adaptor;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public interface ListenerAdaptor {
    boolean supports(EventListener object);

    void onEvent(EventListener object, Iterable<CacheEvent> cacheEvents);
}
