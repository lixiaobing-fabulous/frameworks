package com.lxb.cache.listener.adaptor;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;
import com.lxb.cache.listener.listeners.UpdateListener;

public class UpdateListenerAdaptor implements ListenerAdaptor {

    @Override
    public boolean supports(EventListener listener) {
        return listener instanceof UpdateListener;
    }

    @Override
    public void onEvent(EventListener object, Iterable<CacheEvent> cacheEvents) {
        UpdateListener createListener = (UpdateListener) object;
        createListener.onUpdate(cacheEvents);
    }
}
