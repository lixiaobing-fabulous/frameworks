package com.lxb.rpc.exception;


/**
 * 缓存异常
 */
public class CacheException extends LafException {

    private static final long serialVersionUID = 5016557248305452870L;

    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, String errorCode) {
        super(message, errorCode);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }
}
