package com.lxb.cache.listener.adaptor;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;
import com.lxb.cache.listener.listeners.CreateListener;

public class CreateListenerAdaptor implements ListenerAdaptor {

    @Override
    public boolean supports(EventListener listener) {
        return listener instanceof CreateListener;
    }

    @Override
    public void onEvent(EventListener object, Iterable<CacheEvent> cacheEvents) {
        CreateListener createListener = (CreateListener) object;
        createListener.onCreate(cacheEvents);
    }
}
