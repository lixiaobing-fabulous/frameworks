package com.lxb.extension.exception;

/**
 * 插件异常
 */
public class PluginException extends RuntimeException {

    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

}
