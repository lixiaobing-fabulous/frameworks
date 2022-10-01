package com.lxb.rpc.registry.nacos.dubbo;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

@Extension(value = "nacos", provider = "dubbo")
@ConditionalOnClass("com.alibaba.nacos.api.naming.NamingService")
public class DubboNacosRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(String name, URL url, Backup backup) {
        return new DubboNacosRegistry(name, url, backup);
    }
}
