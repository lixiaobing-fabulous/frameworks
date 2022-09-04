package com.lxb.rpc.exception;


/**
 * 方法过载异常
 */
public class MethodOverloadException extends ReflectiveOperationException {

    private static final long serialVersionUID = 2153171428734709778L;

    /**
     * 构造函数
     */
    public MethodOverloadException() {
    }

    /**
     * 构造函数
     *
     * @param message
     */
    public MethodOverloadException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message
     * @param cause
     */
    public MethodOverloadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param cause
     */
    public MethodOverloadException(Throwable cause) {
        super(cause);
    }
}
