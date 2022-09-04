package com.lxb.rpc.exception;

/**
 * 代理包装异常
 */
public class ProxyException extends LafException {

    private static final long serialVersionUID = 7513510706505625106L;

    public ProxyException() {
        super(null, null, false, false, null, false);
    }

    public ProxyException(String message) {
        super(message, null, false, false, null, false);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause, false, false, null, false);
    }

}
