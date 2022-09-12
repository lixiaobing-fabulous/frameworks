package com.lxb.rpc.context;


/**
 * 操作系统类型
 */
public enum OsType {
    LINUX,
    WINDOWS,
    SOLARIS,
    MAC,
    FREEBSD,
    OTHER;

    public static OsType detect(final String type) {
        if (type == null || type.isEmpty()) {
            return OTHER;
        } else if (type.startsWith("Linux")) {
            return LINUX;
        } else if (type.startsWith("Windows")) {
            return WINDOWS;
        } else if (type.contains("SunOS") || type.contains("Solaris")) {
            return SOLARIS;
        } else if (type.contains("Mac")) {
            return MAC;
        } else if (type.contains("FreeBSD")) {
            return FREEBSD;
        } else {
            return OTHER;
        }
    }
}
