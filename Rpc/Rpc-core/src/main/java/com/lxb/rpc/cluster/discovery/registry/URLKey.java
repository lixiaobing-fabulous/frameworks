package com.lxb.rpc.cluster.discovery.registry;


import com.lxb.extension.URL;
import com.lxb.extension.URLOption;
import com.lxb.rpc.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.lxb.rpc.constants.Constants.ALIAS_KEY;
import static com.lxb.rpc.constants.Constants.ALIAS_OPTION;
import static com.lxb.rpc.constants.Constants.GLOBAL_SETTING;
import static com.lxb.rpc.constants.Constants.ROLE_KEY;
import static com.lxb.rpc.constants.Constants.ROLE_OPTION;
import static com.lxb.rpc.constants.Constants.SERVICE_NAME_FUNCTION;
import static com.lxb.rpc.constants.Constants.TYPE_KEY;

/**
 * URL和Key信息
 */
public class URLKey {
    /**
     * URL
     */
    protected URL    url;
    /**
     * Key
     */
    protected String key;
    /**
     * 服务名称
     */
    protected String service;

    public URLKey(final URL url) {
        this.url = url;
        this.service = SERVICE_NAME_FUNCTION.apply(url);
        this.key = buildKey();
    }

    public URLKey(final URLKey key) {
        this.url = key.getUrl();
        this.service = key.getService();
        this.key = key.getKey();
    }

    /**
     * 生成Key
     *
     * @return
     */
    protected String buildKey() {
        return null;
    }

    public URL getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public String getService() {
        return service;
    }

    public String getProtocol() {
        return url.getProtocol();
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        return url.getPort();
    }

    /**
     * 获取接口名称
     *
     * @return 接口名称
     */
    public String getInterface() {
        return url.getPath();
    }

    public String getString(final URLOption<String> option) {
        return url.getString(option);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        URLKey key1 = (URLKey) o;

        return key.equals(key1.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * 注册的URLKey
     */
    public static class RegKey extends URLKey {

        public RegKey(URL url) {
            super(url);
        }

        @Override
        protected String buildKey() {
            //生产者和消费者都需要注册
            //注册:协议+服务+分组+角色（消费者、提供者）
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ALIAS_KEY, url.getString(ALIAS_OPTION));
            parameters.put(ROLE_KEY, url.getString(ROLE_OPTION));
            URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), service, parameters);
            return u.toString();
        }
    }

    /**
     * 订阅集群的URLKey
     */
    public static class ClusterKey extends URLKey {

        public ClusterKey(URL url) {
            super(url);
        }

        @Override
        protected String buildKey() {
            //接口集群订阅:协议+服务+分组+集群类型
            Map<String, String> parameters = new HashMap<>();
            parameters.put(TYPE_KEY, "cluster");
            parameters.put(ALIAS_KEY, url.getString(ALIAS_OPTION));
            URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), service, parameters);
            return u.toString();
        }
    }

    /**
     * 订阅配置的URLKey
     */
    public static class ConfigKey extends URLKey {

        public ConfigKey(URL url) {
            super(url);
        }

        @Override
        protected String buildKey() {
            //分组信息可能在参数里面，可以在子类里面覆盖
            if (StringUtils.isEmpty(url.getPath())) {
                //全局配置订阅
                return GLOBAL_SETTING;
            } else {
                //接口配置订阅:协议+服务+分组+角色（消费者、提供者）+配置类型
                Map<String, String> parameters = new HashMap<>();
                parameters.put(TYPE_KEY, "config");
                parameters.put(ALIAS_KEY, url.getString(ALIAS_OPTION));
                parameters.put(ROLE_KEY, url.getString(ROLE_OPTION));
                //配置还是以接口进行订阅，而不是服务
                URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), parameters);
                return u.toString();
            }
        }
    }
}
