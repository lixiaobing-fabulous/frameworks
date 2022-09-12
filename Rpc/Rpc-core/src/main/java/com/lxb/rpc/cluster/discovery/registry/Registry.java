package com.lxb.rpc.cluster.discovery.registry;



import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.config.Configure;
import com.lxb.rpc.cluster.discovery.naming.Registar;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 注册中心接口
 */
public interface Registry extends Registar, Configure {

    /**
     * 打开
     *
     * @return 异步Future
     */
    CompletableFuture<Void> open();

    /**
     * 关闭
     *
     * @return 异步Future
     */
    CompletableFuture<Void> close();

    /**
     * 注册接口
     *
     * @param url url
     * @return 异步Future
     */
    CompletableFuture<URL> register(URL url);

    /**
     * 反注册接口
     *
     * @param url url
     * @return 异步Future
     */
    default CompletableFuture<URL> deregister(URL url) {
        return deregister(url, 0);
    }

    /**
     * 反注册接口
     *
     * @param url           url
     * @param maxRetryTimes 最大重试次数<br/>
     *                      <li>>0 最大重试次数</li>
     *                      <li>=0 不重试</li>
     *                      <li><0 无限重试</li>
     * @return 异步Future
     */
    CompletableFuture<URL> deregister(URL url, int maxRetryTimes);


    Supplier<Integer> ID_GENERATOR = new Supplier<Integer>() {

        protected AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Integer get() {
            return counter.incrementAndGet();
        }
    };

}
