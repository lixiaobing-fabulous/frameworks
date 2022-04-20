package com.lxb.cache.listener.adaptor;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;
import com.lxb.cache.listener.listeners.ExpireListener;

public class ExpireListenerAdaptor implements ListenerAdaptor {

    @Override
    public boolean supports(EventListener listener) {
        return listener instanceof ExpireListener;
    }

    @Override
    public void onEvent(EventListener object, Iterable<CacheEvent> cacheEvents) {
        ExpireListener createListener = (ExpireListener) object;
        createListener.onExpire(cacheEvents);
    }
}

