package com.lxb.rpc.exception;


/**
 * 连接关闭异常
 *
 * @date: 2019/2/21
 */
public class ChannelClosedException extends TransportException {

    private static final long serialVersionUID = 4401440531171871948L;

    public ChannelClosedException() {
    }

    public ChannelClosedException(boolean retry) {
        super(retry);
    }

    public ChannelClosedException(String message) {
        super(message);
    }

    public ChannelClosedException(String message, boolean retry) {
        super(message, retry);
    }

    public ChannelClosedException(String message, String errorCode, boolean retry) {
        super(message, errorCode, retry);
    }

    public ChannelClosedException(String message, String errorCode) {
        super(message, errorCode);
    }

    public ChannelClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelClosedException(String message, Throwable cause, boolean retry) {
        super(message, cause, retry);
    }

    public ChannelClosedException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode);
    }

    public ChannelClosedException(String message, Throwable cause, String errorCode, boolean retry) {
        super(message, cause, errorCode, retry);
    }

    public ChannelClosedException(Throwable cause) {
        super(cause);
    }

    public ChannelClosedException(Throwable cause, boolean retry) {
        super(cause, retry);
    }

    public ChannelClosedException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public ChannelClosedException(Throwable cause, String errorCode, boolean retry) {
        super(cause, errorCode, retry);
    }
}
