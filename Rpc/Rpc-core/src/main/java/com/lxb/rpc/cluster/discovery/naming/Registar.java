package com.lxb.rpc.cluster.discovery.naming;


import com.lxb.extension.Extensible;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Region;
import com.lxb.rpc.cluster.discovery.Normalizer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 集群节点命名目录服务
 */
@Extensible("namingService")
public interface Registar extends Region, Normalizer {

    /**
     * 订阅接口(主要指对一个URL变化的订阅)
     *
     * @param url     the url
     * @param handler the listener
     * @return the boolean
     */
    boolean subscribe(URL url, ClusterHandler handler);

    /**
     * 取消订阅接口
     *
     * @param url     the url
     * @param handler
     * @return the boolean
     */
    boolean unsubscribe(URL url, ClusterHandler handler);

    /**
     * 获取目录服务URL
     *
     * @return
     */
    URL getUrl();

    Supplier<Integer> REGISTAR_ID_GENERATOR = new Supplier<Integer>() {

        protected AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Integer get() {
            return counter.incrementAndGet();
        }
    };

}
