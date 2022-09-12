package com.lxb.rpc.registry.broadcast;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

/**
 * hazelcast注册中心实现插件
 */
@Extension(value = "broadcast")
@ConditionalOnClass("com.hazelcast.core.Hazelcast")
public class BroadcastRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(final String name, final URL url, final Backup backup) {
        return new BroadcastRegistry(name, url, backup);
    }
}
