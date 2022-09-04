package com.lxb.rpc.exception;


/**
 * 反射异常
 */
public class ReflectionException extends LafException {

    private static final long serialVersionUID = -8626672349453027035L;

    public ReflectionException() {
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, String errorCode) {
        super(message, errorCode);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }
}
