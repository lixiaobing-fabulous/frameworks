package com.lxb.rpc.exception;


/**
 * 序列化异常
 */
public class SerializerException extends LafException {

    private static final long serialVersionUID = 9184777691669750392L;

    public SerializerException() {
        super(null, null, false, false, null, false);
    }

    public SerializerException(String message) {
        super(message, null, false, false, null, false);
    }

    public SerializerException(String message, String errorCode) {
        super(message, null, false, false, errorCode, false);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause, false, false, null, false);
    }

    public SerializerException(String message, Throwable cause, String errorCode) {
        super(message, cause, false, false, errorCode, false);
    }

    public SerializerException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause, false, false, null, false);
    }

    public SerializerException(Throwable cause, String errorCode) {
        super(cause == null ? null : cause.toString(), cause, false, false, errorCode, false);
    }
}
