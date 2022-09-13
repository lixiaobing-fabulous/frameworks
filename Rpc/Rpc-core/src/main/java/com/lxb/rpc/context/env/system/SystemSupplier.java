package com.lxb.rpc.context.env.system;


import com.lxb.extension.Extension;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.context.EnvironmentSupplier;
import com.lxb.rpc.context.OsType;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.lxb.rpc.context.EnvironmentSupplier.SYSTEM_ORDER;

/**
 * 系统环境变量提供者，从环境变量和系统属性获取，支持黑白名单和变量重命名。<br/>
 * 从类路径"system_env"和"META-INF/system_env"按照顺序找到第一个资源文件进行加载
 * 黑白名单规则:以'-'开头的是黑名单,'*'代表所有<br/>
 * 重命名规则:key=key1,key2,key3,按照顺序找到第一个值
 */
@Extension(value = "system", order = SYSTEM_ORDER)
public class SystemSupplier implements EnvironmentSupplier {

    @Override
    public Map<String, String> environment() {
        //从系统环境获取
        Map<String, String> env = System.getenv();
        Map<String, String> result = new HashMap<>(env.size());
        for (Map.Entry<String, String> entry : env.entrySet()) {
            result.putIfAbsent(entry.getKey(), entry.getValue());
        }
        //从系统属性和jvm参数获取
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> result.putIfAbsent(k.toString(), v.toString()));
        //从系统运行时获取
        result.putIfAbsent(Constants.KEY_MEMORY, String.valueOf(getMemory()));
        result.putIfAbsent(Constants.KEY_CPU_CORES, String.valueOf(getCpuCores()));
        result.put(Constants.KEY_PID, String.valueOf(getPid()));
        result.put(Constants.KEY_START_TIME, String.valueOf(getStartTime()));
        result.put(Constants.KEY_OS_TYPE, getOsType().toString());

        return result;
    }

    /**
     * 获取进程ID
     *
     * @return 进程ID
     */
    protected long getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = name.indexOf('@');
        if (indexOf > 0) {
            name = name.substring(0, indexOf);
        }
        return Long.parseLong(name);
    }

    /**
     * 获取进程启动时间
     *
     * @return 进程启动时间
     */
    protected long getStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    /**
     * 获取内存大小
     *
     * @return 内存大小
     */
    protected long getMemory() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osmxb.getTotalPhysicalMemorySize();
    }

    /**
     * 获取操作系统版本
     *
     * @return
     */
    protected OsType getOsType() {
        return OsType.detect(System.getProperty(Constants.KEY_OS_NAME));
    }

    /**
     * 获取CPU核数
     *
     * @return
     */
    protected int getCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
