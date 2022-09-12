package com.lxb.rpc.registry.consul;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

@Extension(value = "consul")
public class ConsulRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(String name, URL url, Backup backup) {
        return new ConsulRegistry(name, url, backup);
    }
}
