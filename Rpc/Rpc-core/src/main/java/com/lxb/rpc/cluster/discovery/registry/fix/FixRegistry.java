package com.lxb.rpc.cluster.discovery.registry.fix;



import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.event.ConfigEvent;
import com.lxb.rpc.cluster.discovery.naming.fix.FixRegistar;
import com.lxb.rpc.cluster.discovery.registry.AbstractRegistry;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 固定注册中心
 */
public class FixRegistry extends AbstractRegistry {


    /**
     * 构造函数
     *
     * @param url url
     */
    public FixRegistry(URL url) {
        super(url);
    }

    @Override
    protected RegistryPilot create() {
        return new FixController(this);
    }

    /**
     * 控制器
     */
    protected static class FixController extends RegistryController<FixRegistry> {

        /**
         * 注册中心
         */
        protected FixRegistar registar;

        /**
         * 构造函数
         *
         * @param registry 注册中心
         */
        public FixController(final FixRegistry registry) {
            super(registry);
            registar = new FixRegistar(registry.getUrl());
        }

        @Override
        protected CompletableFuture<Void> doConnect() {
            return registar.open();
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ClusterBooking booking) {
            registar.subscribe(booking.getUrl(), booking);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doUnsubscribe(final ClusterBooking booking) {
            registar.unsubscribe(booking.getUrl(), booking);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ConfigBooking booking) {
            booking.handle(new ConfigEvent(this, null, -1, new HashMap<>()));
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doUnsubscribe(final ConfigBooking booking) {
            return CompletableFuture.completedFuture(null);
        }
    }


}
