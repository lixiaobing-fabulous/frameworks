package com.lxb.rpc.exception;


import com.lxb.rpc.transport.message.Header;

/**
 * Rpc异常
 */
public class RpcException extends LafException {

    private static final long serialVersionUID = 3269562091618562124L;

    protected transient Header header;

    public RpcException() {
        super(null, null, false, false, null, false);
    }

    public RpcException(String message) {
        super(message, null, false, false, null, false);
    }

    public RpcException(String message, boolean retry) {
        super(message, null, false, false, null, retry);
    }

    public RpcException(String message, String errorCode, boolean retry) {
        super(message, null, false, false, errorCode, retry);
    }

    public RpcException(String message, String errorCode) {
        super(message, null, false, false, errorCode, false);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause, false, false, null, false);
    }

    public RpcException(String message, Throwable cause, boolean retry) {
        super(message, cause, false, false, null, retry);
    }

    public RpcException(String message, Throwable cause, String errorCode) {
        super(message, cause, false, false, errorCode, false);
    }

    public RpcException(String message, Throwable cause, String errorCode, boolean retry) {
        super(message, cause, errorCode, retry);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, boolean retry) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, retry);
    }

    public RpcException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, false);
    }

    public RpcException(Throwable cause, boolean retry) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, retry);
    }

    public RpcException(Throwable cause, String errorCode) {
        super(cause == null ? null : cause.toString(), cause, false, false, errorCode, false);
    }

    public RpcException(Throwable cause, String errorCode, boolean retry) {
        super(cause == null ? null : cause.toString(), cause, false, false, errorCode, retry);
    }

    public RpcException(Header header, String message) {
        super(message, null, false, false, null, false);
        this.header = header;
    }

    public RpcException(Header header, String message, String errorCode) {
        super(message, null, false, false, errorCode, false);
        this.header = header;
    }

    public RpcException(Header header, Throwable cause) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, false);
        this.header = header;
    }

    public RpcException(Header header, String message, boolean retry) {
        super(message, null, false, false, null, retry);
        this.header = header;
    }

    public RpcException(Header header, String message, String errorCode, boolean retry) {
        super(message, null, false, false, errorCode, retry);
        this.header = header;
    }

    public RpcException(Header header, Throwable cause, boolean retry) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, retry);
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
