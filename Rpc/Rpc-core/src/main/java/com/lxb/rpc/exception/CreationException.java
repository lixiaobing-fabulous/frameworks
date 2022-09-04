package com.lxb.rpc.exception;



/**
 * 实例化异常
 */
public class CreationException extends ReflectionException {

    private static final long serialVersionUID = -7067155283140779827L;

    public CreationException() {
    }

    public CreationException(String message) {
        super(message);
    }

    public CreationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreationException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public CreationException(Throwable cause) {
        super(cause);
    }

    public CreationException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }
}
