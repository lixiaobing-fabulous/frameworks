package com.lxb.rpc.cluster.discovery.naming;


import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.backup.file.FileBackup;
import com.lxb.rpc.cluster.discovery.registry.Registry;
import com.lxb.rpc.cluster.discovery.registry.RegistryFactory;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.exception.InitializationException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.lxb.rpc.constants.Constants.*;


/**
 * 抽象注册中心工厂类
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    protected Map<String, Registry> registries = new ConcurrentHashMap<>();

    protected static final Function<URL, String> REGISTRY_KEY_FUNC = o -> o.toString(false, true, Constants.ADDRESS_OPTION.getName());

    @Override
    public Registry getRegistry(URL url) {
        return getRegistry(url, REGISTRY_KEY_FUNC);
    }

    @Override
    public Registry getRegistry(final URL url, final Function<URL, String> function) {
        if (url == null) {
            throw new InitializationException("url can not be null.");
        }
        Function<URL, String> keyFunc = function == null ? KEY_FUNC : function;
        return registries.computeIfAbsent(keyFunc.apply(url), o -> createRegistry(url));
    }

    /**
     * 创建注册中心
     *
     * @param url
     * @return
     */
    protected Registry createRegistry(final URL url) {
        // 创建注册中心实例
        try {
            String name   = url.getString(REGISTRY_NAME_KEY, url.getProtocol());
            Backup backup = null;
            //判断是否开启备份
            boolean enabled = url.getBoolean(REGISTRY_BACKUP_ENABLED_OPTION);
            if (enabled) {
                //改注册中心设置的备份路径
                String path = url.getString(REGISTRY_BACKUP_PATH_OPTION);
                if (path == null || path.isEmpty()) {
                    //用户目录
                    path = System.getProperty(KEY_USER_HOME) + File.separator + "rpc_backup";
                }
                String application = url.getString(KEY_APPNAME, "no_app");
                File directory = new File(path + File.separator + name + File.separator + application + File.separator);
                backup = new FileBackup(directory, url.getInteger(REGISTRY_BACKUP_DATUM_OPTION));
            }
            return createRegistry(name, url, backup);
        } catch (IOException e) {
            throw new InitializationException("Error occurs while creating registry. caused by: ", e);
        }
    }

    /**
     * 创建注册中心
     *
     * @param name   名称
     * @param url    url
     * @param backup 备份
     * @return 注册中心
     */
    protected Registry createRegistry(String name, URL url, Backup backup) {
        return null;
    }

}
