package com.lxb.rpc.exception;


/**
 * 初始化一次
 */
public class InitializationException extends LafException {

    private static final long serialVersionUID = -9027367467194186279L;

    public InitializationException() {
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

    public InitializationException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }
}
