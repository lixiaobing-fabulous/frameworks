package com.lxb.rpc.cluster.discovery.registry.fix;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

/**
 * 固定注册中心工厂类
 */
@Extension(value = "fix")
public class FixRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected Registry createRegistry(URL url) {
        return new FixRegistry(url);
    }
}
