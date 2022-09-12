package com.lxb.rpc.cluster.discovery;


import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Region;
import com.lxb.rpc.constants.Version;
import com.lxb.rpc.context.GlobalContext;
import com.lxb.rpc.util.SystemClock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lxb.rpc.constants.Constants.*;
import static com.lxb.rpc.util.Maps.put;

/**
 * 标准化
 */
public interface Normalizer {

    /**
     * 构造URL的参数
     */
    Function<URL, Map<String, String>> PARAMETER_FUNCTION = url -> {
        Map<String, String> params = new HashMap<>();
        put(params, ALIAS_OPTION.getName(), url.getString(ALIAS_OPTION));
        put(params, BUILD_VERSION_KEY, String.valueOf(Version.BUILD_VERSION));
        put(params, VERSION_KEY, GlobalContext.getString(PROTOCOL_VERSION_KEY));
        put(params, KEY_APPAPTH, GlobalContext.getString(KEY_APPAPTH));
        put(params, KEY_APPID, GlobalContext.getString(KEY_APPID));
        put(params, KEY_APPNAME, GlobalContext.getString(KEY_APPNAME));
        put(params, KEY_APPINSID, GlobalContext.getString(KEY_APPINSID));
        put(params, Region.REGION, GlobalContext.getString(Region.REGION));
        put(params, Region.DATA_CENTER, GlobalContext.getString(Region.DATA_CENTER));
        //保留和原有的代码兼容
        put(params, JAVA_VERSION_KEY, GlobalContext.getString(KEY_JAVA_VERSION));
        put(params, ROLE_OPTION.getName(), url.getString(ROLE_OPTION));
        put(params, SERIALIZATION_OPTION.getName(), url.getString(SERIALIZATION_OPTION));
        put(params, TIMEOUT_OPTION.getName(), url.getString(TIMEOUT_OPTION.getName()));
        put(params, WEIGHT_OPTION.getName(), url.getString(WEIGHT_OPTION.getName()));
        put(params, DYNAMIC_OPTION.getName(), url.getString(DYNAMIC_OPTION.getName()));
        put(params, SERVICE_NAME_KEY, url.getString(SERVICE_NAME_KEY));
        put(params, SSL_ENABLE_KEY, "true", (key, value) -> url.getBoolean(SSL_ENABLE));
        put(params, GENERIC_KEY, "true", (key, value) -> url.getBoolean(GENERIC_OPTION));
        put(params, TIMESTAMP_KEY, String.valueOf(SystemClock.now()));

        return params;
    };

    /**
     * 由refer与export的url到注册中心存储url的转换function
     */
    Function<URL, URL> NORMALIZE_FUNCTION = url -> new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), PARAMETER_FUNCTION.apply(url));

    /**
     * 标准化
     *
     * @param url url
     * @return
     */
    default URL normalize(final URL url) {
        return url == null ? null : NORMALIZE_FUNCTION.apply(url);
    }

}
