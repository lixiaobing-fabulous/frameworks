package com.lxb.rpc.apm.trace;

import java.util.Map;

/**
 * 跟踪会话
 */
public interface Tracer {
    /**
     * 主线程开始
     *
     * @param name      跟踪名称
     * @param component 组件名称
     * @param tags      标签
     */
    void begin(String name, String component, Map<String, String> tags);

    /**
     * 主线程做快照
     */
    default void snapshot(){

    }

    /**
     * 主线程结束
     */
    default void prepare(){

    }

    /**
     * 异步线程恢复
     */
    default void restore(){

    }

    /**
     * 异步线程调用结束
     *
     * @param throwable 异常
     */
    void end(Throwable throwable);
}
