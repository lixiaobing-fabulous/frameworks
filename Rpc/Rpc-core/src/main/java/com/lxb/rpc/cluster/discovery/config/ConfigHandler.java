package com.lxb.rpc.cluster.discovery.config;


import com.lxb.rpc.cluster.discovery.event.ConfigEvent;
import com.lxb.rpc.event.EventHandler;

/**
 * 配置处理器
 */
@FunctionalInterface
public interface ConfigHandler extends EventHandler<ConfigEvent> {
}
