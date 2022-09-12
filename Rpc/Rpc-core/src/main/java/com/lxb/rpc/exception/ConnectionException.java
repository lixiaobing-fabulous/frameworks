package com.lxb.rpc.exception;


/**
 * 连接异常
 */
public class ConnectionException extends TransportException {

    private static final long serialVersionUID = 4401440531171871948L;

    public ConnectionException() {
    }

    public ConnectionException(boolean retry) {
        super(retry);
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, boolean retry) {
        super(message, retry);
    }

    public ConnectionException(String message, String errorCode, boolean retry) {
        super(message, errorCode, retry);
    }

    public ConnectionException(String message, String errorCode) {
        super(message, errorCode);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(String message, Throwable cause, boolean retry) {
        super(message, cause, retry);
    }

    public ConnectionException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public ConnectionException(String message, Throwable cause, String errorCode, boolean retry) {
        super(message, cause, errorCode, retry);
    }

    public ConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, boolean retry) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, retry);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

    public ConnectionException(Throwable cause, boolean retry) {
        super(cause, retry);
    }

    public ConnectionException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public ConnectionException(Throwable cause, String errorCode, boolean retry) {
        super(cause, errorCode, retry);
    }
}
