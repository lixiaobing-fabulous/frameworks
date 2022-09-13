package com.lxb.rpc.protocol.message;



/**
 * 应答消息
 */
public class ResponseMessage<T>  {
    /**
     * 响应结果
     */
    protected T response;



    public T getPayLoad() {
        return response;
    }

    public void setPayLoad(T payload) {
        this.response = payload;
    }

    public boolean isRequest() {
        return false;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "response=" + response +
                '}';
    }
}
