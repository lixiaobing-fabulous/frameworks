package com.lxb.cache.listener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ServiceLoader;

import com.lxb.cache.listener.adaptor.ListenerAdaptor;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-19
 */
public class ListenerAdaptorRegistry {
    private List<ListenerAdaptor> listenerAdaptors;

    public ListenerAdaptorRegistry() {
        listenerAdaptors = new ArrayList<>();
        ServiceLoader.load(ListenerAdaptor.class).iterator().forEachRemaining(listenerAdaptors::add);
    }

    public void onEvent(EventListener listener, Iterable<CacheEvent> cacheEvents) {
        for (ListenerAdaptor listenerAdaptor : listenerAdaptors) {
            if (listenerAdaptor.supports(listener)) {
                listenerAdaptor.onEvent(listener, cacheEvents);
            }
        }
    }


}
