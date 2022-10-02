package com.lxb.rpc.protocol.message;


import com.lxb.rpc.cotext.RequestContext;

/**
 * @date: 8/1/2019
 */
public class RequestMessage<T> {

    /**
     * 请求体信息
     */
    protected T payload;

    public T getPayLoad() {
        return payload;
    }

    public boolean isConsumer() {
        return true;
    }

    public RequestContext getContext() {
        return new RequestContext();
    }
}
