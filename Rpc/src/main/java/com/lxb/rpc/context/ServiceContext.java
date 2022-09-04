package com.lxb.rpc.context;

import static com.lxb.rpc.util.ServiceLoaders.loadDefault;

public interface ServiceContext {
    ServiceContext DEFAULT = loadDefault(ServiceContext.class);

    boolean registerService(String serviceName, Object service);

    Object getService(String serviceName);

}
