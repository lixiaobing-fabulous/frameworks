package com.lxb.rpc.loadbalance;

import com.lxb.rpc.service.ServiceInstance;

import java.util.List;

import static com.lxb.rpc.util.ServiceLoaders.loadDefault;

public interface ServiceInstanceSelector {

    ServiceInstanceSelector DEFAULT = loadDefault(ServiceInstanceSelector.class);

    ServiceInstance select(List<ServiceInstance> serviceInstances);
}
