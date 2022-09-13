package com.lxb.rpc.context;


import java.util.Map;

/**
 * 环境变量提供者
 */
@FunctionalInterface
public interface EnvironmentSupplier {

    int SYSTEM_ORDER = 100;

    int COMMAND_ORDER = SYSTEM_ORDER + 10;

    int SPRING_ORDER = COMMAND_ORDER + 10;

    /**
     * 提供的环境变量
     *
     * @return 环境变量
     */
    Map<String, String> environment();

}
