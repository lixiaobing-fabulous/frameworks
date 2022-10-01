package com.lxb.rpc.util.thread;


import com.lxb.extension.Parametric;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 线程池
 */
public interface ThreadPool extends ExecutorService {

    /**
     * 输出运行时信息
     *
     * @return 运行时信息
     */
    Map<String, Object> dump();

    /**
     * 配置参数
     *
     * @param parametric 参数
     */
    void configure(Parametric parametric);
}
