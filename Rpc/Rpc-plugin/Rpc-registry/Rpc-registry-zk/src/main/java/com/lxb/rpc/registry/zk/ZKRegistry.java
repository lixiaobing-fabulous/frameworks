package com.lxb.rpc.registry.zk;


import com.lxb.extension.URL;
import com.lxb.extension.URLOption;
import com.lxb.rpc.cluster.Shard;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.event.ConfigEvent;
import com.lxb.rpc.cluster.discovery.registry.AbstractRegistry;
import com.lxb.rpc.cluster.discovery.registry.URLKey;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.event.Publisher;
import com.lxb.rpc.event.UpdateEvent;
import com.lxb.rpc.util.Futures;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.api.CreateOption;
import org.apache.curator.x.async.api.DeleteOption;
import org.apache.curator.x.async.api.ExistsOption;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.lxb.rpc.Plugin.JSON;
import static com.lxb.rpc.constants.Constants.CONNECT_TIMEOUT_OPTION;
import static com.lxb.rpc.event.UpdateEvent.UpdateType.FULL;
import static com.lxb.rpc.event.UpdateEvent.UpdateType.UPDATE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.POST_INITIALIZED_EVENT;
import static org.apache.curator.x.async.api.CreateOption.createParentsIfNeeded;
import static org.apache.curator.x.async.api.CreateOption.setDataIfExists;
import static org.apache.zookeeper.CreateMode.EPHEMERAL;

/**
 * Zookeeper注册中心
 */
public class ZKRegistry extends AbstractRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZKRegistry.class);

    /**
     * session超时时间参数
     */
    public static final URLOption<Integer> SESSION_TIMEOUT = new URLOption<>("sessionTimeout", 15000);

    /**
     * 目标地址
     */
    protected String address;
    /**
     * session过期时间
     */
    protected int sessionTimeout;
    /**
     * 连接超时时间
     */
    protected int connectionTimeout;
    /**
     * 根路径
     */
    protected String                   root;
    /**
     * 服务的路径函数 /根路径/service/接口/别名/consumer|provider/ip:port
     */
    protected Function<URLKey, String> serviceFunction;
    /**
     * 集群的路径函数 /根路径/service/接口/别名/provider
     */
    protected Function<URLKey, String> clusterFunction;
    /**
     * 接口配置路径函数(接口级全局配置) /根路径/config/接口/consumer|provider
     */
    protected Function<URLKey, String> configFunction;

    /**
     * 构造函数
     *
     * @param name   名称
     * @param url    url
     * @param backup 备份
     */
    public ZKRegistry(final String name, final URL url, final Backup backup) {
        super(name, url, backup);
        this.address = URL.valueOf(url.getString(Constants.ADDRESS_OPTION), "zookeeper", 2181, null).getAddress();
        this.sessionTimeout = url.getInteger(SESSION_TIMEOUT);
        this.connectionTimeout = url.getInteger(CONNECT_TIMEOUT_OPTION);
        this.root = new RootPath().apply(url);
        this.serviceFunction = new ServicePath(root);
        this.clusterFunction = new ClusterPath(root);
        this.configFunction = new ConfigPath(root);
    }

    @Override
    protected RegistryPilot create() {
        return new ZKController(this);
    }

    @Override
    protected Registion createRegistion(final URLKey key) {
        return new Registion(key, serviceFunction.apply(key));
    }

    /**
     * ZK控制器
     */
    protected static class ZKController extends RegistryController<ZKRegistry> {

        /**
         * zk异步Curator对象
         */
        protected AsyncCuratorFramework curator;

        /**
         * 构造函数
         *
         * @param registry 注册中心
         */
        public ZKController(ZKRegistry registry) {
            super(registry);
        }

        @Override
        protected ClusterBooking createClusterBooking(final URLKey key) {
            return new ZKClusterBooking(key, this::dirty, getPublisher(key.getKey()), registry.clusterFunction.apply(key));
        }

        @Override
        protected ConfigBooking createConfigBooking(final URLKey key) {
            return new ZKConfigBooking(key, this::dirty, getPublisher(key.getKey()), registry.configFunction.apply(key));
        }

        @Override
        protected CompletableFuture<Void> doConnect() {
            return Futures.call(future -> {
                CuratorFramework client = CuratorFrameworkFactory.builder().connectString(registry.address)
                        .sessionTimeoutMs(registry.sessionTimeout)
                        .connectionTimeoutMs(registry.connectionTimeout)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
                client.start();
                client.getConnectionStateListenable().addListener((curator, state) -> {
                    if (!isOpen()) {
                        doDisconnect().whenComplete((v, t) -> future.completeExceptionally(new IllegalStateException("controller is closed.")));
                    } else if (state.isConnected()) {
                        logger.warn("zk connection state is changed to " + state + ".");
                        if (future.isDone()) {
                            //重新注册
                            registers.forEach((k, r) -> addBookingTask(registers, r, this::doRegister));
                        } else {
                            future.complete(null);
                        }
                    } else {
                        //会自动重连
                        logger.warn("zk connection state is changed to " + state + ".");
                    }
                });
                curator = AsyncCuratorFramework.wrap(client);
            });
        }

        @Override
        protected CompletableFuture<Void> doDisconnect() {
            if (curator != null) {
                curator.unwrap().close();
            }
            return super.doDisconnect();
        }

        @Override
        protected CompletableFuture<Void> doRegister(final Registion registion) {
            Set<ExistsOption> existsOptions = new HashSet<ExistsOption>() {{
                add(ExistsOption.createParentsIfNeeded);
            }};
            Set<CreateOption> createOptions = new HashSet<CreateOption>() {{
                add(createParentsIfNeeded);
                add(setDataIfExists);
            }};
            return Futures.call(future -> {
                //判断节点是否存在
                curator.checkExists().withOptions(existsOptions).forPath(registion.getPath()).whenComplete((stat, exist) -> {
                    //若存在，删除临时节点
                    if (stat != null) {
                        try {
                            curator.unwrap().delete().forPath(registion.getPath());
                        } catch (Exception ignored) {
                        }
                    }
                    //添加临时节点
                    curator.create().withOptions(createOptions, EPHEMERAL)
                            .forPath(registion.getPath(), registion.getUrl().toString().getBytes(UTF_8)).whenComplete((n, err) -> {
                        if (err != null) {
                            future.completeExceptionally(err);
                        } else {
                            future.complete(null);
                        }
                    });
                });
            });
        }

        @Override
        protected CompletableFuture<Void> doDeregister(final Registion registion) {
            //删除节点
            Set<DeleteOption> deleteOptions = new HashSet<DeleteOption>() {{
                add(DeleteOption.quietly);
            }};
            return Futures.call(future -> curator.delete().withOptions(deleteOptions).forPath(registion.getPath()).whenComplete((n, err) -> {
                if (err != null) {
                    future.completeExceptionally(err);
                } else {
                    future.complete(null);
                }
            }));
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ClusterBooking booking) {
            return Futures.call(future -> {
                ZKClusterBooking zkBooking = (ZKClusterBooking) booking;
                //添加监听
                PathChildrenCache cache = new MyPathChildrenCache(curator.unwrap(), booking.getPath(), true, () -> isOpen());
                //启动监听
                cache.start(POST_INITIALIZED_EVENT);
                zkBooking.setChildrenCache(cache);
                future.complete(null);
                cache.getListenable().addListener((client, event) -> {
                    List<ClusterEvent.ShardEvent> events = new ArrayList<>();
                    UpdateEvent.UpdateType        type   = UPDATE;
                    switch (event.getType()) {
                        case INITIALIZED:
                            type = FULL;
                            List<ChildData> children = event.getInitialData();
                            if (children != null) {
                                children.forEach(childData -> addEvent(events, ClusterEvent.ShardEventType.ADD, childData));
                            }
                            break;
                        case CHILD_ADDED:
                            addEvent(events, ClusterEvent.ShardEventType.ADD, event.getData());
                            break;
                        case CHILD_UPDATED:
                            addEvent(events, ClusterEvent.ShardEventType.UPDATE, event.getData());
                            break;
                        case CHILD_REMOVED:
                            addEvent(events, ClusterEvent.ShardEventType.DELETE, event.getData());
                            break;

                    }
                    booking.handle(new ClusterEvent(registry, null, type, zkBooking.getStat().incrementAndGet(), events));
                });
            });
        }

        /**
         * 添加事件
         *
         * @param events    事件集合
         * @param type      事件类型
         * @param childData 节点数据
         */
        protected void addEvent(final List<ClusterEvent.ShardEvent> events, final ClusterEvent.ShardEventType type, final ChildData childData) {
            byte[] data = childData.getData();
            if (data != null) {
                events.add(new ClusterEvent.ShardEvent(new Shard.DefaultShard(URL.valueOf(new String(data, UTF_8))), type));
            }
        }

        @Override
        protected CompletableFuture<Void> doUnsubscribe(final ClusterBooking booking) {
            PathChildrenCache cache = ((ZKClusterBooking) booking).getChildrenCache();
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ignored) {
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ConfigBooking booking) {
            return Futures.call(future -> {
                ZKConfigBooking zkBooking = (ZKConfigBooking) booking;
                CuratorFramework client = curator.unwrap();
                Stat pathStat = client.checkExists().creatingParentsIfNeeded().forPath(booking.getPath());
                if (pathStat == null) {
                    client.create().creatingParentsIfNeeded().forPath(booking.getPath(), new byte[0]);
                }
                NodeCache cache = new MyNodeCache(client, booking.getPath(), () -> isOpen());
                cache.start();
                zkBooking.setNodeCache(cache);
                future.complete(null);
                cache.getListenable().addListener(() -> {
                    ChildData childData = cache.getCurrentData();
                    Map<String, String> datum;
                    if (childData == null) {
                        //被删掉了
                        datum = new HashMap<>();
                    } else {
                        byte[] data = childData.getData();
                        if (data != null && data.length > 0) {
                            datum = JSON.get().parseObject(new String(data, UTF_8), Map.class);
                        } else {
                            datum = new HashMap<>();
                        }
                    }
                    booking.handle(new ConfigEvent(registry, null, zkBooking.getStat().incrementAndGet(), datum));
                });
            });
        }

        @Override
        protected CompletableFuture<Void> doUnsubscribe(final ConfigBooking booking) {
            NodeCache cache = ((ZKConfigBooking) booking).getNodeCache();
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ignored) {
                }
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * 配置订阅
     */
    protected static class ZKClusterBooking extends ClusterBooking {
        /**
         * zk节点监听cache
         */
        protected PathChildrenCache childrenCache;
        /**
         * 事件版本
         */
        protected AtomicLong stat = new AtomicLong();

        /**
         * 构造函数
         *
         * @param key       键
         * @param dirty     脏函数
         * @param publisher 通知器
         * @param path      路径
         */
        public ZKClusterBooking(final URLKey key, final Runnable dirty, final Publisher<ClusterEvent> publisher, final String path) {
            super(key, dirty, publisher, path);
        }

        public PathChildrenCache getChildrenCache() {
            return childrenCache;
        }

        public void setChildrenCache(PathChildrenCache childrenCache) {
            this.childrenCache = childrenCache;
        }

        public AtomicLong getStat() {
            return stat;
        }
    }

    /**
     * 配置订阅
     */
    protected static class ZKConfigBooking extends ConfigBooking {
        /**
         * zk节点监听cache
         */
        protected NodeCache nodeCache;
        /**
         * 事件版本
         */
        protected AtomicLong stat = new AtomicLong();

        /**
         * 构造函数
         *
         * @param key       键
         * @param dirty     脏函数
         * @param publisher 通知器
         * @param path      路径
         */
        public ZKConfigBooking(final URLKey key, final Runnable dirty, final Publisher<ConfigEvent> publisher, final String path) {
            super(key, dirty, publisher, path);
        }

        public NodeCache getNodeCache() {
            return nodeCache;
        }

        public void setNodeCache(NodeCache nodeCache) {
            this.nodeCache = nodeCache;
        }

        public AtomicLong getStat() {
            return stat;
        }
    }

    /**
     * PathChildrenCache
     */
    protected static class MyPathChildrenCache extends PathChildrenCache {

        protected Supplier<Boolean> open;

        public MyPathChildrenCache(CuratorFramework client, String path, boolean cacheData, Supplier<Boolean> open) {
            super(client, path, cacheData);
            this.open = open;
        }

        @Override
        protected void handleException(Throwable e) {
            if (open.get()) {
                //防止退出的时候打印异常
                super.handleException(e);
            }
        }
    }

    /**
     * NodeCache
     */
    protected static class MyNodeCache extends NodeCache {

        protected Supplier<Boolean> open;

        public MyNodeCache(CuratorFramework client, String path, Supplier<Boolean> open) {
            super(client, path);
            this.open = open;
        }

        @Override
        protected void handleException(Throwable e) {
            if (open.get()) {
                //防止退出的时候打印异常
                super.handleException(e);
            }
        }
    }
}
