package com.lxb.rpc.event;


/**
 * @date: 2019/3/21
 */
public class AsyncResult<T> implements Event {

    protected T result;

    protected boolean success;

    protected Throwable throwable;

    public AsyncResult() {
        this.success = true;
    }

    public AsyncResult(boolean success) {
        this.success = success;
    }

    public AsyncResult(T result) {
        this.result = result;
        this.success = true;
    }

    public AsyncResult(Throwable throwable) {
        this.success = false;
        this.throwable = throwable;
    }

    public AsyncResult(T result, Throwable throwable) {
        this.success = false;
        this.result = result;
        this.throwable = throwable;
    }

    public AsyncResult(AsyncResult source, T result) {
        this.success = source.success;
        this.throwable = source.throwable;
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
