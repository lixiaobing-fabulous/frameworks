package com.lxb.rpc.util;

import java.util.ServiceLoader;


public class ServiceLoaders {
    public static <T> T loadDefault(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass).iterator().next();
    }


}
