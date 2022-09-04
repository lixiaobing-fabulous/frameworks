package com.lxb.extension.condition;



import com.lxb.extension.exception.PluginException;

import java.lang.annotation.Annotation;

/**
 * Java版本判断
 */
public class OnJavaCondition implements Condition {
    @Override
    public boolean match(final ClassLoader classLoader, final Class clazz, final Annotation annotation) {
        String version = System.getProperty("java.version");
        ConditionalOnJava onJava = (ConditionalOnJava) annotation;
        int javaVersion = getVersion(clazz, version);
        int targetVersion = getVersion(clazz, onJava.value());
        switch (onJava.range()) {
            case OLDER_THAN:
                return javaVersion < targetVersion;
            case EQUAL_OR_NEWER:
                return javaVersion >= targetVersion;
            default:
                return false;
        }
    }

    /**
     * 获取版本
     *
     * @param clazz
     * @param version
     * @return
     */
    protected int getVersion(final Class clazz, final String version) {
        try {
            String[] parts = version.trim().split("\\.");
            if (parts.length > 1) {
                return Integer.parseInt(parts[0]) * 1000 + Integer.parseInt(parts[1]);
            } else {
                return Integer.parseInt(parts[0]) * 1000;
            }
        } catch (Exception e) {
            throw new PluginException(clazz.getName() + ": Error parse java version: " + version);
        }
    }
}
