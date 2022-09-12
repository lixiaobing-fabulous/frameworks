package com.lxb.rpc.cluster.discovery.naming;


import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.event.EventHandler;
/**
 * 集群处理器
 */
public interface ClusterHandler extends EventHandler<ClusterEvent> {
}
