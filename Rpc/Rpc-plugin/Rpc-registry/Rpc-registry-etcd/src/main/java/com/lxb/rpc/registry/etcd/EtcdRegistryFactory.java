package com.lxb.rpc.registry.etcd;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

/**
 * ETCD注册中心
 */
@Extension(value = "etcd")
@ConditionalOnClass("io.etcd.jetcd.Client")
public class EtcdRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(final String name, final URL url, final Backup backup) {
        return new EtcdRegistry(name, url, backup);
    }
}
