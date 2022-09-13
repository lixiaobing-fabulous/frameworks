package com.lxb.rpc.context;



import com.lxb.extension.Extensible;

import java.util.Map;

/**
 * 接口配置变化监听器
 *
 * @date: 2019/6/21
 */
@Extensible("configEventHandler")
public interface ConfigEventHandler {

    /**
     * IP权限配置
     */
    int PERMISSION_ORDER = 0;

    /**
     * 熔断配置
     */
    int BREAKER_ORDER = 10;

    /**
     * 跨机房访问首选机房配置顺序
     */
    int CIRCUIT_ORDER = 40;

    /**
     * 自适应负载均衡配置
     */
    int ADAPTIVE_ORDER = 50;

    /**
     * 全局配置事件order
     */
    int GLOBAL_ORDER = 100;

    /**
     * 接口级业务相关配置事件order
     */
    int BIZ_ORDER = 200;

    /**
     * 处理配置
     *
     * @param className 接口类名
     * @param oldAttrs  老配置
     * @param newAttrs  新配置
     */
    void handle(String className, Map<String, String> oldAttrs, Map<String, String> newAttrs);

    /**
     * 获取动态更新的Key，这些Key不会拼接到URL里面
     *
     * @return
     */
    default String[] getKeys() {
        return new String[0];
    }

}
