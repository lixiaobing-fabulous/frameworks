package com.lxb.rpc.cluster.discovery.registry.memory;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

/**
 * 内存注册中心工厂类
 */
@Extension("memory")
public class MemoryRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(final URL url) {
        return new MemoryRegistry(url);
    }
}
