package com.lxb.rpc.cluster.discovery.config;


import com.lxb.extension.Extensible;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.Normalizer;


/**
 * 集群配置服务
 */
@Extensible("configure")
public interface Configure extends Normalizer {

    /**
     * 订阅接口(主要指对一个URL变化的订阅)
     *
     * @param url     the url
     * @param handler the config event handler
     */
    boolean subscribe(URL url, ConfigHandler handler);

    /**
     * 取消订阅接口
     *
     * @param url     the url
     * @param handler
     */
    boolean unsubscribe(URL url, ConfigHandler handler);

}
