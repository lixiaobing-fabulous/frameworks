package com.lxb.rpc.registry.consul;


import com.lxb.extension.URL;
import com.lxb.extension.URLOption;
import com.lxb.rpc.cluster.Shard;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.event.ConfigEvent;
import com.lxb.rpc.cluster.discovery.registry.AbstractRegistry;
import com.lxb.rpc.cluster.discovery.registry.URLKey;
import com.lxb.rpc.codec.serialization.TypeReference;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.context.Environment;
import com.lxb.rpc.context.GlobalContext;
import com.lxb.rpc.event.Publisher;
import com.lxb.rpc.event.UpdateEvent;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.util.Futures;
import com.lxb.rpc.util.SystemClock;
import com.lxb.rpc.util.Timer;
import com.lxb.rpc.util.network.Ping;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.BlockingQueryOptions;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.CheckStatus;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.KeyValue;
import io.vertx.ext.consul.Service;
import io.vertx.ext.consul.ServiceEntryList;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.ext.consul.ServiceQueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.lxb.rpc.Plugin.ENVIRONMENT;
import static com.lxb.rpc.Plugin.JSON;
import static com.lxb.rpc.constants.Constants.ALIAS_OPTION;
import static com.lxb.rpc.constants.Constants.KEY_APPNAME;
import static com.lxb.rpc.constants.Constants.ROLE_OPTION;
import static com.lxb.rpc.util.StringUtils.SEMICOLON_COMMA_WHITESPACE;
import static com.lxb.rpc.util.StringUtils.isEmpty;
import static com.lxb.rpc.util.StringUtils.split;
import static com.lxb.rpc.util.Timer.timer;

/**
 * Consul注册中心
 */
public class ConsulRegistry extends AbstractRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);

    /**
     * 服务ID函数
     */
    protected static final Predicate<String> SERVICE_LOSS = s -> s != null && s.contains("does not have associated TTL");
    public static final String CONSUL_TTL = "consul.ttl";
    public static final String CONSUL_LEASE_INTERVAL = "consul.leaseInterval";
    public static final String CONSUL_BOOKING_INTERVAL = "consul.bookingInterval";
    public static final String CONSUL_ACLTOKEN = "consul.aclToken";
    public static final String             CONSUL_TIMEOUT            = "consul.timeout";
    public static final URLOption<Boolean> SERVICE_WITH_GROUP_OPTION = new URLOption<>("consul.serviceWithGroup", true);

    protected List<String> addresses;

    protected int ttl;
    protected int leaseInterval;
    protected int bookingInterval;
    protected String aclToken;
    protected int timeout;
    protected boolean serviceWithGroup;
    protected Vertx vertx;

    public ConsulRegistry(String name, URL url, Backup backup) {
        super(name, url, backup);
        String address = url.getString(Constants.ADDRESS_OPTION);
        this.addresses = !address.isEmpty() ? Arrays.asList(split(address, SEMICOLON_COMMA_WHITESPACE)) : Collections.emptyList();
        this.ttl = Math.max(url.getPositive(CONSUL_TTL, 30000), 30000);
        this.leaseInterval = url.getPositive(CONSUL_LEASE_INTERVAL, Math.min(ttl / 4, 10000));
        this.bookingInterval = url.getPositive(CONSUL_BOOKING_INTERVAL, 5000);
        this.timeout = url.getPositive(CONSUL_TIMEOUT, 5000);
        this.aclToken = url.getString(CONSUL_ACLTOKEN);
        this.serviceWithGroup = url.getBoolean(SERVICE_WITH_GROUP_OPTION);
    }

    @Override
    protected RegistryPilot create() {
        return new ConsulRegistryController(this);
    }

    @Override
    protected void doOpen() {
        this.vertx = Vertx.vertx();
        super.doOpen();
    }

    @Override
    protected void doClose() {
        if (vertx != null) {
            vertx.close();
        }
        super.doClose();
    }

    /**
     * 随机获取地址
     *
     * @return
     */
    protected String randomAddress() {
        int size = addresses.size();
        switch (size) {
            case 0:
                return null;
            case 1:
                return addresses.get(0);
            default:
                return addresses.get(ThreadLocalRandom.current().nextInt(size));
        }
    }

    @Override
    protected Registion createRegistion(final URLKey key) {
        return new ConsulRegistion(key, createServiceName(key));
    }

    /**
     * 构建服务名称
     *
     * @param key key
     * @return 服务名称
     */
    protected String createServiceName(final URLKey key) {
        return serviceWithGroup ? key.getService() + ":::" + key.getUrl().getString(ALIAS_OPTION) : key.getService();
    }

    /**
     * Consul控制器
     */
    protected static class ConsulRegistryController<T extends ConsulRegistry> extends RegistryController<T> {
        /**
         * Consul可贺的
         */
        protected ConsulClient client;
        /**
         * 编码后的应用名称
         */
        protected String appPath;

        public ConsulRegistryController(T registry) {
            super(registry);
            String appName = GlobalContext.getString(KEY_APPNAME);
            if (appName == null || appName.isEmpty()) {
                appPath = "";
            } else {
                try {
                    appPath = "/" + URLEncoder.encode(appName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    appPath = "";
                }
            }
        }

        @Override
        protected ClusterBooking createClusterBooking(final URLKey key) {
            return new ConsulClusterBooking(key, this::dirty, getPublisher(key.getKey()), registry.createServiceName(key));
        }

        @Override
        protected ConfigBooking createConfigBooking(final URLKey key) {
            //按照接口粒度进行配置
            String path = key.getInterface() + "/" + key.getString(ROLE_OPTION) + appPath;
            return new ConsulConfigBooking(key, this::dirty, getPublisher(key.getKey()), path);
        }

        @Override
        protected CompletableFuture<Void> doConnect() {
            CompletableFuture<Void> result = new CompletableFuture<>();
            URL url = URL.valueOf(registry.randomAddress(), "http", 8500, null);
            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(url.getHost()).setPort(url.getPort()).setAclToken(registry.aclToken).setTimeout(registry.timeout);
            client = ConsulClient.create(registry.vertx, options);
            client.agentInfo(r -> {
                if (r.failed()) {
                    result.completeExceptionally(r.cause());
                } else {
                    JsonObject config = r.result().getJsonObject("Config");
                    updateDc(config == null ? null : config.getString("Datacenter"));
                    result.complete(null);
                }
            });
            return result;
        }

        @Override
        protected CompletableFuture<Void> doDisconnect() {
            if (client != null) {
                client.close();
            }
            return super.doDisconnect();
        }

        @Override
        protected CompletableFuture<Void> doRegister(final Registion registion) {
            if (Constants.SIDE_CONSUMER.equals(registion.getString(ROLE_OPTION))) {
                //消费者不注册
                return CompletableFuture.completedFuture(null);
            }
            ConsulRegistion cr = (ConsulRegistion) registion;
            cr.transportErrors.set(0);
            //注册，服务状态异常后自动注销的最小时间是1分钟
            ServiceOptions opts = new ServiceOptions()
                    .setName(cr.getPath())
                    .setId(cr.getInsId())
                    .setTags(getTags(cr.getUrl()))
                    .setMeta(getMeta(cr.getUrl()))
                    .setCheckOptions(new CheckOptions().setTtl(registry.ttl + "ms").setStatus(CheckStatus.PASSING).setDeregisterAfter("1m"))
                    .setAddress(cr.getHost())
                    .setPort(cr.getPort());

            CompletableFuture<Void> result = new CompletableFuture<>();
            client.registerService(opts, r -> {
                if (r.failed()) {
                    result.completeExceptionally(r.cause());
                } else {
                    cr.expireTime = SystemClock.now() + registry.ttl;
                    long time = SystemClock.now() + registry.leaseInterval + ThreadLocalRandom.current().nextInt(2000);
                    addLeaseTimer(registion, time);
                    result.complete(null);
                }
            });
            return result;
        }

        @Override
        protected CompletableFuture<Void> doDeregister(final Registion registion) {
            CompletableFuture<Void> result = new CompletableFuture<>();
            client.deregisterService(((ConsulRegistion) registion).getInsId(), r -> result.complete(null));
            return result;
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ClusterBooking booking) {
            return Futures.call(future -> {
                ConsulClusterBooking ccb = (ConsulClusterBooking) booking;
                doUpdate(ccb);
                addClusterTimer(ccb);
                future.complete(null);
            });
        }

        @Override
        protected CompletableFuture<Void> doSubscribe(final ConfigBooking booking) {
            return Futures.call(future -> {
                ConsulConfigBooking ccb = (ConsulConfigBooking) booking;
                doUpdate(ccb);
                addConfigTimer(ccb);
                future.complete(null);
            });
        }

        /**
         * 更新机房信息
         *
         * @param dataCenter 数据中心
         */
        protected void updateDc(String dataCenter) {
            if (isEmpty(dataCenter)) {
                return;
            }
            //设置数据中心
            String[] parts = split(dataCenter, ':');
            String region = null;
            if (parts.length >= 2) {
                //region:dataCenter
                region = parts[0];
                dataCenter = parts[1];
            }
            Environment environment = ENVIRONMENT.get();
            if (!isEmpty(region) && isEmpty(GlobalContext.getString(REGION))) {
                GlobalContext.put(REGION, region);
                environment.put(REGION, region);
            }
            if (!isEmpty(dataCenter) && isEmpty(GlobalContext.getString(DATA_CENTER))) {
                GlobalContext.put(DATA_CENTER, dataCenter);
                environment.put(DATA_CENTER, dataCenter);
            }
        }

        /**
         * 添加续约定时器
         *
         * @param registion  注册
         * @param expireTime 过期时间
         */
        protected void addLeaseTimer(final Registion registion, final long expireTime) {
            timer().add(new Timer.DelegateTask("Lease-" + registion.getKey(), expireTime, () -> {
                if (isOpen() && connected.get() && registers.containsKey(registion.getKey())) {
                    doLease((ConsulRegistion) registion);
                }
            }));
        }

        /**
         * 添加集群订阅定时器
         *
         * @param booking 订阅
         */
        protected void addClusterTimer(final ConsulClusterBooking booking) {
            //加上随机时间
            long time = SystemClock.now() + registry.bookingInterval + ThreadLocalRandom.current().nextInt(2000);
            timer().add(new Timer.DelegateTask("Cluster-" + booking.getKey(), time, () -> {
                if (isOpen() && connected.get() && clusters.containsKey(booking.getKey())) {
                    try {
                        doUpdate(booking);
                    } finally {
                        //再次添加集群订阅定时器
                        addClusterTimer(booking);
                    }
                }
            }));
        }

        /**
         * 续约操作
         *
         * @param registion 注册
         */
        protected void doLease(final ConsulRegistion registion) {
            String serviceId = "service:" + registion.getInsId();
            client.passCheck(serviceId, r -> {
                if (r.succeeded()) {
                    registion.expireTime = SystemClock.now() + registry.ttl;
                    registion.transportErrors.set(0);
                    //再次添加续约任务
                    addLeaseTimer(registion, SystemClock.now() + registry.leaseInterval + ThreadLocalRandom.current().nextInt(2000));
                } else {
                    if (Ping.detectDead(r.cause())) {
                        //连接异常，可能Consul宕机了
                        if (registion.transportErrors.incrementAndGet() == 3) {
                            //重连
                            logger.error(String.format("Transport error occurs more than 3 times, caused by %s. reconnect....", r.cause().getMessage()));
                            //先关闭连接，再重连
                            doDisconnect().whenComplete((v, t) -> {
                                if (isOpen()) {
                                    reconnect(new CompletableFuture<>(), 0, registry.maxConnectRetryTimes);
                                }
                            });
                        } else {
                            //网络异常加快检查
                            addLeaseTimer(registion, SystemClock.now() + 2000);
                        }
                    } else if (SERVICE_LOSS.test(r.cause().getMessage())) {
                        //服务注册信息丢失，可能服务端重启了
                        doRegister(registion);
                    } else {
                        //再次添加续约任务
                        addLeaseTimer(registion, SystemClock.now() + registry.leaseInterval + ThreadLocalRandom.current().nextInt(2000));
                    }
                }
            });
        }

        /**
         * 更新集群
         *
         * @param booking 订阅
         */
        protected void doUpdate(final ConsulClusterBooking booking) {
            if (registry.serviceWithGroup) {
                client.healthServiceNodes(booking.getPath(), true, booking);
            } else {
                client.healthServiceNodesWithOptions(booking.getPath(), true,
                        new ServiceQueryOptions().setTag(booking.getUrl().getString(ALIAS_OPTION)),
                        booking);
            }
        }

        /**
         * 添加配置订阅定时器
         *
         * @param booking 订阅
         */
        protected void addConfigTimer(final ConsulConfigBooking booking) {
            //加上随机时间
            long time = SystemClock.now() + registry.bookingInterval + ThreadLocalRandom.current().nextInt(2000);
            timer().add(new Timer.DelegateTask("Cluster-" + booking.getKey(), time, () -> {
                if (isOpen() && connected.get() && configs.containsKey(booking.getKey())) {
                    try {
                        doUpdate(booking);
                    } finally {
                        //再次添加配置订阅定时器
                        addConfigTimer(booking);
                    }
                }
            }));
        }

        /**
         * 更新配置
         *
         * @param booking 配置订阅
         */
        protected void doUpdate(final ConsulConfigBooking booking) {
            if (!isOpen() || !connected.get() || !configs.containsKey(booking.getKey())) {
                return;
            }
            BlockingQueryOptions options = new BlockingQueryOptions().setIndex(booking.getVersion() < 0 ? 0 : booking.getVersion());
            client.getValueWithOptions(booking.getPath(), options, booking);
        }

        /**
         * 获取注册的元数据
         *
         * @param url url
         * @return 元数据
         */
        protected Map<String, String> getMeta(final URL url) {
            return PARAMETER_FUNCTION.apply(url);
        }

        /**
         * 获取标签信息
         *
         * @param url url
         * @return 标签
         */
        protected List<String> getTags(final URL url) {
            if (registry.serviceWithGroup) {
                return null;
            }
            return Collections.singletonList(url.getString(ALIAS_OPTION));
        }

    }

    /**
     * 注册信息
     */
    protected static class ConsulRegistion extends Registion {
        /**
         * 过期时间
         */
        protected long expireTime;
        /**
         * 唯一ID
         */
        protected String insId;
        /**
         * 连续网络异常
         */
        protected AtomicInteger transportErrors = new AtomicInteger();

        public ConsulRegistion(URLKey key, String path) {
            super(key, path);
            insId = createInsId();
        }

        protected String createInsId() {
            return UUID.randomUUID().toString();
        }

        /**
         * 是否过期了
         *
         * @return 过期标识
         */
        public boolean isExpire() {
            return expireTime <= SystemClock.now();
        }

        /**
         * 获取唯一ID
         *
         * @return
         */
        public String getInsId() {
            return insId;
        }
    }

    /**
     * 配置订阅
     */
    protected static class ConsulConfigBooking extends ConfigBooking implements Handler<AsyncResult<KeyValue>> {

        public ConsulConfigBooking(URLKey key, Runnable dirty, Publisher<ConfigEvent> publisher, String path) {
            super(key, dirty, publisher, path);
        }

        @Override
        public void handle(AsyncResult<KeyValue> result) {
            if (result.succeeded()) {
                KeyValue keyValue = result.result();
                if (keyValue != null && keyValue.getModifyIndex() > version) {
                    try {
                        Map<String, String> map = JSON.get().parseObject(keyValue.getValue(), new TypeReference<Map<String, String>>() {
                        });
                        handle(new ConfigEvent(this, null, keyValue.getModifyIndex(), map));
                    } catch (SerializerException e) {
                        //解析出错，设置新的版本，跳过错误的数据
                        setVersion(keyValue.getModifyIndex());
                        logger.error(String.format("Error occurs while parsing config of %s\n%s", getInterface(), keyValue.getValue()));
                    }
                } else if (version < 0) {
                    handle(new ConfigEvent(this, null, 0, new HashMap<>()));
                }
            }
        }
    }

    /**
     * 集群订阅
     */
    protected static class ConsulClusterBooking extends ClusterBooking implements Handler<AsyncResult<ServiceEntryList>> {

        public ConsulClusterBooking(URLKey key, Runnable dirty, Publisher<ClusterEvent> publisher, String path) {
            super(key, dirty, publisher, path);
        }

        @Override
        public void handle(AsyncResult<ServiceEntryList> result) {
            if (result.succeeded()) {
                ServiceEntryList services = result.result();
                if (version < 0 || services.getIndex() > version) {
                    String                        defProtocol = GlobalContext.getString(Constants.PROTOCOL_KEY);
                    List<ClusterEvent.ShardEvent> shards      = new LinkedList<>();
                    services.getList().forEach(entry -> {
                        Service service = entry.getService();
                        Map<String, String> meta = service.getMeta();
                        String protocol = meta == null ? null : meta.remove(Constants.PROTOCOL_KEY);
                        protocol = protocol == null || protocol.isEmpty() ? defProtocol : protocol;
                        URL url = new URL(protocol, service.getAddress(), service.getPort(), service.getName(), meta);
                        shards.add(new ClusterEvent.ShardEvent(new Shard.DefaultShard(url), ClusterEvent.ShardEventType.UPDATE));
                    });
                    handle(new ClusterEvent(this, null, UpdateEvent.UpdateType.FULL, services.getIndex(), shards));
                }
            }
        }

    }

}
