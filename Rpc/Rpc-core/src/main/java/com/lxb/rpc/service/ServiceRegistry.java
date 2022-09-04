package com.lxb.rpc.service;

import java.util.List;
import java.util.Map;

import static com.lxb.rpc.util.ServiceLoaders.loadDefault;

/**
 * 服务注册中心
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public interface ServiceRegistry {

    ServiceRegistry DEFAULT = loadDefault(ServiceRegistry.class);

    void initialize(Map<String, Object> config);

    void register(ServiceInstance serviceInstance);

    void deregister(ServiceInstance serviceInstance);

    List<ServiceInstance> getServiceInstances(String serviceName);

    void close();

}
