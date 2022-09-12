package com.lxb.rpc.exception;


/**
 * 协议不支持异常
 */
public class ProtocolException extends RpcException {

    private static final long serialVersionUID = -8258755708955572216L;

    public ProtocolException() {
        super(null, null, false, false, null, false);
    }

    public ProtocolException(String message) {
        super(message, null, false, false, null, false);
    }

    public ProtocolException(String message, String errorCode) {
        super(message, null, false, false, errorCode, false);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause, false, false, null, false);
    }

    public ProtocolException(String message, Throwable cause, String errorCode) {
        super(message, cause, false, false, errorCode, false);
    }

    public ProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, boolean retry) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, retry);
    }

    public ProtocolException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, false);
    }

    public ProtocolException(Throwable cause, String errorCode) {
        super(cause == null ? null : cause.toString(), cause, false, false, errorCode, false);
    }

    /**
     * 协议插件不存在异常
     *
     * @param type 类型
     * @param name 名称
     * @return 异常
     */
    public static ProtocolException noneOf(final String type, final String name) {
        return new ProtocolException(String.format("%s plugin %s is not found.", type, name));
    }
}
