package com.lxb.rpc.util;


import com.lxb.rpc.event.Event;

/**
 * 状态事件
 */
public class StateEvent implements Event {

    /**
     * 开始输出
     */
    public static final int START_EXPORT = 10;
    /**
     * 输出成功
     */
    public static final int SUCCESS_EXPORT = 11;
    /**
     * 输出失败
     */
    public static final int FAIL_EXPORT = 12;
    /**
     * 输出失状态异常
     */
    public static final int FAIL_EXPORT_ILLEGAL_STATE = 13;
    /**
     * 开始打开
     */
    public static final int START_OPEN = 30;
    /**
     * 打开成功
     */
    public static final int SUCCESS_OPEN = 31;
    /**
     * 打开失败
     */
    public static final int FAIL_OPEN = 32;
    /**
     * 打开状态异常
     */
    public static final int FAIL_OPEN_ILLEGAL_STATE = 33;
    /**
     * 开始关闭
     */
    public static final int START_CLOSE = 40;
    /**
     * 关闭成功
     */
    public static final int SUCCESS_CLOSE = 41;

    /**
     * 事件类型
     */
    protected final int type;
    /**
     * 异常
     */
    protected final Throwable throwable;

    public StateEvent(int type) {
        this(type, null);
    }

    public StateEvent(int type, Throwable exception) {
        this.type = type;
        this.throwable = exception;
    }

    public int getType() {
        return type;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
