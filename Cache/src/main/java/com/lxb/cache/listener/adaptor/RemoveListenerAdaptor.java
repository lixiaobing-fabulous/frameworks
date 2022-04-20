package com.lxb.cache.listener.adaptor;

import java.util.EventListener;

import com.lxb.cache.listener.CacheEvent;
import com.lxb.cache.listener.listeners.RemoveListener;

public class RemoveListenerAdaptor implements ListenerAdaptor {

    @Override
    public boolean supports(EventListener listener) {
        return listener instanceof RemoveListener;
    }

    @Override
    public void onEvent(EventListener object, Iterable<CacheEvent> cacheEvents) {
        RemoveListener createListener = (RemoveListener) object;
        createListener.onRemove(cacheEvents);
    }
}
