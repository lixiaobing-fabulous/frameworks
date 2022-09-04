package com.lxb.rpc.loadbalance;

import com.lxb.rpc.service.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机 {@link ServiceInstanceSelector} 实现
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public class RandomServiceInstanceSelector implements ServiceInstanceSelector {

    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances) {
        int size = serviceInstances.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        return serviceInstances.get(index);
    }
}
