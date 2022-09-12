package com.lxb.rpc.cluster.discovery.registry;


import com.lxb.extension.Extensible;
import com.lxb.extension.URL;

import java.util.function.Function;

/**
 * 注册中心工厂
 */
@Extensible("registryfactory")
public interface RegistryFactory {

    Function<URL, String> KEY_FUNC = o -> o.toString(false, false);

    /**
     * 获取注册中心
     *
     * @param url
     * @return
     */
    default Registry getRegistry(URL url) {
        return getRegistry(url, KEY_FUNC);
    }

    /**
     * 获取注册中心
     *
     * @param url
     * @param function
     * @return
     */
    Registry getRegistry(URL url, Function<URL, String> function);

}
