package com.lxb.rpc.registry.nacos.dubbo;


import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lxb.extension.MapParametric;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.registry.URLKey;
import com.lxb.rpc.context.GlobalContext;
import com.lxb.rpc.event.Publisher;
import com.lxb.rpc.registry.nacos.NacosRegistry;
import com.lxb.rpc.util.SystemClock;

import java.util.HashMap;
import java.util.Map;

import static com.lxb.rpc.constants.Constants.*;

/**
 * nacos注册中心
 */
public class DubboNacosRegistry extends NacosRegistry {

    private final static String DUBBO_PROTOCOL_KEY = "protocol";
    private final static String DUBBO_PROTOCOL_VERSION_KEY = "dubbo";
    private final static String DUBBO_SERVICE_VERSION_KEY = "version";
    private final static String DUBBO_SERVICE_REVERSION_KEY = "revision";
    private final static String DUBBO_GROUP_KEY = "group";
    private final static String DUBBO_PATH_KEY = "path";
    private final static String DUBBO_INTERFACE_KEY = "interface";
    private final static String DUBBO_APPLICATION_KEY = "application";
    private final static String DUBBO_TIMESTAMP_KEY = "timestamp";
    private final static String DUBBO_GENERIC_KEY = "generic";
    private final static String DUBBO_PID_KEY = "pid";
    private final static String DUBBO_DEFAULT_KEY = "default";
    private final static String DUBBO_DYNAMIC_KEY = "dynamic";
    private final static String DUBBO_CATEGORY_KEY = "category";
    private final static String DUBBO_ANYHOST_KEY = "anyhost";
    private final static String DUBBO_RELEASE_KEY = "release";

    private final static String DUBBO_RELEASE_VALUE = "2.7.5";
    private final static String DUBBO_PROTOCOL_VALUE = "dubbo";
    private final static String DUBBO_PROTOCOL_VERSION_VALUE = "2.0.2";
    private final static String DUBBO_CATEGORY_PROVIDERS = "providers";
    private final static String DUBBO_CATEGORY_CONSUMERS = "consumers";

    public DubboNacosRegistry(String name, URL url, Backup backup) {
        super(name, url, backup);
    }

    @Override
    protected RegistryPilot create() {
        return new DubboNacosRegistryController(this);
    }

    @Override
    protected Registion createRegistion(final URLKey key) {
        return new DubboNacosRegistion(key);
    }

    @Override
    protected URLKey.RegKey buildRegKey(URL url) {
        return new DubboNacosRegKey(url);
    }

    @Override
    protected URLKey.ClusterKey buildClusterKey(URL url) {
        return new DubboNacosClusterKey(url);
    }

    /**
     * 注册的URLKey
     */
    public static class DubboNacosRegKey extends URLKey.RegKey {

        public DubboNacosRegKey(URL url) {
            super(url);
        }

        @Override
        protected String buildKey() {
            //生产者和消费者都需要注册
            //注册:协议+服务+分组+服务版+tag+角色（消费者、提供者）
            Map<String, String> parameters = new HashMap<>();
            //分组
            parameters.put(ALIAS_KEY, url.getString(ALIAS_OPTION));
            //服务版本
            parameters.put(SERVICE_VERSION_OPTION.getName(), url.getString(SERVICE_VERSION_OPTION));
            //tag
            String tagKey = url.getString(TAG_KEY_OPTION);
            parameters.put(tagKey, url.getString(tagKey, ""));
            //角色
            parameters.put(ROLE_KEY, url.getString(ROLE_OPTION));
            //生成url
            URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), service, parameters);
            return u.toString();
        }
    }

    /**
     * 订阅集群的URLKey
     */
    public static class DubboNacosClusterKey extends URLKey.ClusterKey {

        public DubboNacosClusterKey(URL url) {
            super(url);
        }

        @Override
        protected String buildKey() {
            //生产者和消费者都需要注册
            //注册:协议+服务+分组+服务版+集群类型
            Map<String, String> parameters = new HashMap<>();
            parameters.put(TYPE_KEY, "cluster");
            //分组
            parameters.put(ALIAS_KEY, url.getString(ALIAS_OPTION));
            //服务版本
            parameters.put(SERVICE_VERSION_OPTION.getName(), url.getString(SERVICE_VERSION_OPTION));
            //生成url
            URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), service, parameters);
            return u.toString();
        }
    }


    /**
     * dubbo nacos控制器
     */
    protected static class DubboNacosRegistryController extends NacosRegistryController {

        /**
         * 构造函数
         *
         * @param registry 注册中心对象
         */
        public DubboNacosRegistryController(DubboNacosRegistry registry) {
            super(registry);
        }

        @Override
        protected ClusterBooking createClusterBooking(URLKey key) {
            return new DubboNacosClusterBooking(key, this::dirty, getPublisher(key.getKey()));
        }
    }

    /**
     * dubbo nacos内部集群订阅
     */
    protected static class DubboNacosClusterBooking extends NacosClusterBooking {

        /**
         * 构造方法
         *
         * @param key
         * @param dirty
         * @param publisher
         */
        public DubboNacosClusterBooking(URLKey key, Runnable dirty, Publisher<ClusterEvent> publisher) {
            super(key, dirty, publisher);
        }

        @Override
        protected URL createShardUrl(String defProtocol, Instance instance) {
            Map<String, String> meta = instance.getMetadata();
            String alias = meta.remove(DUBBO_GROUP_KEY);
            alias = alias == null ? ALIAS_OPTION.getValue() : alias;
            if (!instance.isEnabled() || !url.getString(ALIAS_OPTION).equals(alias)) {
                return null;
            }
            String protocol = meta.remove(DUBBO_PROTOCOL_KEY);
            protocol = protocol == null || protocol.isEmpty() ? defProtocol : protocol;
            String ifaceName = meta.remove(DUBBO_PATH_KEY);
            String serviceVersion = meta.remove(DUBBO_SERVICE_VERSION_KEY);
            //重置alias
            meta.put(ALIAS_OPTION.getName(), alias);
            //重置serviceVersion
            meta.put(SERVICE_VERSION_OPTION.getName(), serviceVersion);
            //创建URL
            return new URL(protocol, instance.getIp(), instance.getPort(), ifaceName, meta);
        }
    }

    /**
     * 注册信息
     */
    protected static class DubboNacosRegistion extends NacosRegistion {

        /**
         * 构造方法
         *
         * @param key
         */
        public DubboNacosRegistion(URLKey key) {
            super(key);
        }

        @Override
        protected Instance createInstance(URL url) {
            MapParametric<String, Object> context = new MapParametric(GlobalContext.getContext());
            //metadata
            Map<String, String> meta = new HashMap<>();
            String side = url.getString(ROLE_OPTION);
            meta.put(ROLE_OPTION.getName(), side);
            meta.put(DUBBO_RELEASE_KEY, DUBBO_RELEASE_VALUE);
            meta.put(DUBBO_PROTOCOL_VERSION_KEY, DUBBO_PROTOCOL_VERSION_VALUE);
            meta.put(DUBBO_PID_KEY, context.getString(KEY_PID));
            meta.put(DUBBO_INTERFACE_KEY, url.getPath());
            String serviceVersion = url.getString(SERVICE_VERSION_OPTION);
            if (serviceVersion != null && !serviceVersion.isEmpty()) {
                meta.put(DUBBO_SERVICE_VERSION_KEY, serviceVersion);
                meta.put(DUBBO_SERVICE_REVERSION_KEY, serviceVersion);
            }
            meta.put(DUBBO_GENERIC_KEY, String.valueOf(url.getBoolean(GENERIC_OPTION)));
            meta.put(DUBBO_PATH_KEY, url.getPath());
            meta.put(DUBBO_DEFAULT_KEY, "true");
            meta.put(DUBBO_PROTOCOL_KEY, DUBBO_PROTOCOL_VALUE);
            meta.put(DUBBO_APPLICATION_KEY, context.getString(KEY_APPNAME));
            meta.put(DUBBO_DYNAMIC_KEY, String.valueOf(url.getBoolean(DYNAMIC_OPTION)));
            meta.put(DUBBO_CATEGORY_KEY, SIDE_PROVIDER.equals(side) ? DUBBO_CATEGORY_PROVIDERS : DUBBO_CATEGORY_CONSUMERS);
            meta.put(DUBBO_ANYHOST_KEY, "true");
            meta.put(DUBBO_GROUP_KEY, url.getString(ALIAS_OPTION));
            meta.put(DUBBO_TIMESTAMP_KEY, String.valueOf(SystemClock.now()));
            String tagKey = url.getString(TAG_KEY_OPTION);
            String tag = url.getString(tagKey);
            if (tag != null && !tag.isEmpty()) {
                meta.put(tagKey, tag);
            }
            //创建instace
            Instance instance = new Instance();
            instance.setIp(url.getHost());
            instance.setPort(url.getPort());
            instance.setMetadata(meta);
            return instance;
        }
    }


}
