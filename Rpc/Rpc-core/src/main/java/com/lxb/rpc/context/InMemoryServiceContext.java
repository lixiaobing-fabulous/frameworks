package com.lxb.rpc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryServiceContext implements ServiceContext {
    private final Map<String, Object> store = new ConcurrentHashMap();

    @Override
    public boolean registerService(String serviceName, Object service) {
        return store.putIfAbsent(serviceName, service) == null;
    }

    @Override
    public Object getService(String serviceName) {
        return store.get(serviceName);
    }
}
