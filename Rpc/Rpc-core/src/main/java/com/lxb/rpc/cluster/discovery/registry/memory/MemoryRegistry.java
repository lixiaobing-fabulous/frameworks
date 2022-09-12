package com.lxb.rpc.cluster.discovery.registry.memory;


import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Region;
import com.lxb.rpc.cluster.Shard;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.event.ConfigEvent;
import com.lxb.rpc.cluster.discovery.registry.AbstractRegistry;
import com.lxb.rpc.cluster.discovery.registry.URLKey;
import com.lxb.rpc.event.UpdateEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;

/**
 * 内存注册中心，便于测试
 */
public class MemoryRegistry extends AbstractRegistry {

    protected Region region;

    /**
     * 配置地址
     */
    protected Map<String, AtomicReference<Config>> configDatum = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param url url
     */
    public MemoryRegistry(URL url) {
        this(null, url, null);
    }

    /**
     * 构造函数
     *
     * @param name 名称
     * @param url  url
     */
    public MemoryRegistry(String name, URL url) {
        this(name, url, null);
    }

    /**
     * 构造函数
     *
     * @param name   名称
     * @param url    url
     * @param backup 备份
     */
    public MemoryRegistry(String name, URL url, Backup backup) {
        super(name, url, backup);
        region = new DefaultRegion(url.getString(Region.REGION),
                url.getString(Region.DATA_CENTER));
    }

    @Override
    public String getRegion() {
        return region.getRegion();
    }

    @Override
    public String getDataCenter() {
        return region.getDataCenter();
    }

    @Override
    protected RegistryPilot create() {
        return new MemoryController(this);
    }

    /**
     * 更新配置
     *
     * @param url    url
     * @param values 配置
     */
    public void update(final URL url, final Map<String, String> values) {
        if (url == null) {
            return;
        }
        URLKey key = new URLKey.ConfigKey(url);
        update(key.getKey(), values);
    }

    /**
     * 更新配置
     *
     * @param key    键
     * @param values 配置
     */
    public void update(final String key, final Map<String, String> values) {
        if (key == null) {
            return;
        }
        AtomicReference<Config> ref = configDatum.computeIfAbsent(key, k -> new AtomicReference<>());
        Config oldDatum;
        long version;
        while (true) {
            oldDatum = ref.get();
            version = oldDatum == null ? 0 : oldDatum.getVersion() + 1;
            final Config newDatum = new Config(version, values == null ? new HashMap<>() : new HashMap<>(values));
            if (ref.compareAndSet(oldDatum, newDatum)) {
                state.whenOpen(c -> ((MemoryController) c).update(key, newDatum));
                return;
            }
            LockSupport.parkNanos(1);
        }
    }


    /**
     * 创建分片
     *
     * @param url url
     * @return 分片
     */
    protected Shard createShard(final URL url) {
        //TODO 目前只支持单URL
        String region = url.getString("region");
        String dataCenter = url.getString("dataCenter");
        region = region == null || region.isEmpty() ? this.region.getRegion() : region;
        dataCenter = dataCenter == null || dataCenter.isEmpty() ? this.region.getDataCenter() : dataCenter;

        return new Shard.DefaultShard(url.getAddress(), region, dataCenter, url.getProtocol(),
                url, 100, Shard.ShardState.INITIAL);
    }

    /**
     * 控制器
     */
    protected static class MemoryController extends RegistryController<MemoryRegistry> {

        /**
         * 注册的地址
         */
        protected Map<String, AtomicReference<Topology>> urls = new ConcurrentHashMap<>();

        /**
         * 构造函数
         *
         * @param registry 注册中心
         */
        public MemoryController(final MemoryRegistry registry) {
            super(registry);
        }

        /**
         * 配置发生变化
         *
         * @param key    键
         * @param config 配置
         */
        public void update(final String key, final Config config) {
            ConfigBooking booking = configs.get(key);
            if (booking != null) {
                booking.handle((new ConfigEvent(this, null, config.getVersion(), config.getData())));
            }
        }

        /**
         * 原子更新
         *
         * @param url      url
         * @param ref      拓扑引用
         * @param function 函数
         * @return 最新的版本
         */
        protected long update(final URLKey url,
                              final AtomicReference<Topology> ref,
                              final BiFunction<List<URL>, URL, Boolean> function) {
            Topology oldDatum;
            Topology newDatum;
            long version;
            List<URL> urls;
            while (true) {
                oldDatum = ref.get();
                version = oldDatum == null ? 0 : oldDatum.getVersion() + 1;
                urls = oldDatum == null ? new ArrayList<>(0) : new ArrayList<>(oldDatum.urls);
                if (!function.apply(urls, url.getUrl())) {
                    return -1;
                }
                newDatum = new Topology(version, urls);
                if (ref.compareAndSet(oldDatum, newDatum)) {
                    return version;
                }
                LockSupport.parkNanos(1);
            }
        }

        @Override
        protected CompletableFuture<Void> doRegister(final Registion registion) {
            AtomicReference<Topology> ref = urls.computeIfAbsent(registion.getKey(), key -> new AtomicReference<>());
            long version = update(registion, ref, List::add);
            if (version >= 0) {
                ClusterBooking booking = clusters.get(registion.getKey());
                if (booking != null) {
                    List<ClusterEvent.ShardEvent> shards = new ArrayList<>(1);
                    shards.add(new ClusterEvent.ShardEvent(registry.createShard(registion.getUrl()), ClusterEvent.ShardEventType.ADD));
                    booking.handle((new ClusterEvent(this, null, UpdateEvent.UpdateType.UPDATE, version, shards)));
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doDeregister(final Registion registion) {
            AtomicReference<Topology> ref = urls.get(registion.getKey());
            if (ref != null) {
                long version = update(registion, ref, List::remove);
                if (version >= 0) {
                    ClusterBooking booking = clusters.get(registion.getKey());
                    if (booking != null) {
                        List<ClusterEvent.ShardEvent> shards = new ArrayList<>(1);
                        shards.add(new ClusterEvent.ShardEvent(registry.createShard(registion.getUrl()), ClusterEvent.ShardEventType.DELETE));
                        booking.handle((new ClusterEvent(this, null, UpdateEvent.UpdateType.UPDATE, ref.get().getVersion(),
                                shards)));
                    }
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ClusterBooking booking) {
            AtomicReference<Topology> ref = urls.get(booking.getKey());
            Topology                      topology = ref != null ? ref.get() : null;
            List<ClusterEvent.ShardEvent> shards   = new ArrayList<>(topology == null ? 0 : topology.getUrls().size());
            if (topology != null) {
                topology.getUrls().forEach(u -> shards.add(new ClusterEvent.ShardEvent(registry.createShard(u), ClusterEvent.ShardEventType.ADD)));
            }
            long version = topology == null ? 0 : topology.getVersion();
            booking.handle(new ClusterEvent(this, null, UpdateEvent.UpdateType.FULL, version, shards));
            return CompletableFuture.completedFuture(null);
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ConfigBooking booking) {
            AtomicReference<Config> ref = registry.configDatum.get(booking.getKey());
            Config config = ref != null ? ref.get() : null;
            booking.handle(new ConfigEvent(this, null,
                    config == null ? 0 : config.getVersion(),
                    config == null ? new HashMap<>() : config.getData()));
            return CompletableFuture.completedFuture(null);
        }

    }

    /**
     * 集群的拓扑结构数据
     */
    protected static class Topology {
        /**
         * 当前版本
         */
        protected long version;
        /**
         * URL
         */
        protected List<URL> urls;

        /**
         * 构造函数
         *
         * @param version 版本
         * @param urls    url
         */
        public Topology(long version, List<URL> urls) {
            this.version = version;
            this.urls = urls;
        }

        public long getVersion() {
            return version;
        }

        public List<URL> getUrls() {
            return urls;
        }

    }

    /**
     * 配置数据
     */
    protected static class Config {
        /**
         * 当前版本
         */
        protected long version;
        /**
         * 数据
         */
        protected Map<String, String> data;

        /**
         * 构造函数
         *
         * @param version 版本
         * @param data    数据
         */
        public Config(long version, Map<String, String> data) {
            this.version = version;
            this.data = data;
        }

        public long getVersion() {
            return version;
        }

        public Map<String, String> getData() {
            return data;
        }
    }
}
