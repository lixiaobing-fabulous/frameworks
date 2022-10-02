package com.lxb.rpc.apm.health;


import com.lxb.extension.Extensible;

/**
 * 健康诊断
 */
@Extensible("doctor")
public interface Doctor {

    /**
     * 诊断，期望毫秒级返回结果
     *
     * @return
     */
    HealthState diagnose();
}
