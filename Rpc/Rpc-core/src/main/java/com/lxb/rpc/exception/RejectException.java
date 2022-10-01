package com.lxb.rpc.exception;


/**
 * 服务端拒绝该次请求的异常，可以安全重试，关闭过程会用到
 */
public class RejectException extends LafException {

    public RejectException() {
        super(true);
    }

    public RejectException(boolean retry) {
        super(retry);
    }

    public RejectException(String message) {
        super(message, true);
    }

    public RejectException(String message, boolean retry) {
        super(message, retry);
    }

    public RejectException(String message, String errorCode, boolean retry) {
        super(message, errorCode, retry);
    }

    public RejectException(String message, String errorCode) {
        super(message, errorCode, true);
    }

    public RejectException(String message, Throwable cause) {
        super(message, cause, true);
    }

    public RejectException(String message, Throwable cause, boolean retry) {
        super(message, cause, retry);
    }

    public RejectException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode, true);
    }

    public RejectException(String message, Throwable cause, String errorCode, boolean retry) {
        super(message, cause, errorCode, retry);
    }

    public RejectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode, boolean retry) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode, retry);
    }

    public RejectException(Throwable cause) {
        super(cause, true);
    }

    public RejectException(Throwable cause, boolean retry) {
        super(cause, retry);
    }

    public RejectException(Throwable cause, String errorCode) {
        super(cause, errorCode, true);
    }

    public RejectException(Throwable cause, String errorCode, boolean retry) {
        super(cause, errorCode, retry);
    }

}
