package com.lxb.rpc.exception;


/**
 * 网络异常
 */
public class TransportException extends LafException {

    private static final long serialVersionUID = -7120201445145592600L;

    public TransportException() {
    }

    public TransportException(boolean retry) {
        super(retry);
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, boolean retry) {
        super(message, retry);
    }

    public TransportException(String message, String errorCode, boolean retry) {
        super(message, errorCode, retry);
    }

    public TransportException(String message, String errorCode) {
        super(message, errorCode);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(String message, Throwable cause, boolean retry) {
        super(message, cause, retry);
    }

    public TransportException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public TransportException(String message, Throwable cause, String errorCode, boolean retry) {
        super(message, cause, errorCode, retry);
    }

    public TransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, boolean retry) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, retry);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }

    public TransportException(Throwable cause, boolean retry) {
        super(cause, retry);
    }

    public TransportException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public TransportException(Throwable cause, String errorCode, boolean retry) {
        super(cause, errorCode, retry);
    }
}
