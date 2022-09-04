package com.lxb.rpc.util;

import java.util.ServiceLoader;


public class ServiceLoaders {
    public static <T> T loadDefault(Class<T> serviceClass) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass);
        return loader.iterator().next();
    }


}
