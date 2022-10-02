package com.lxb.rpc.apm.trace;


import com.lxb.extension.Extensible;
import com.lxb.rpc.protocol.message.Invocation;
import com.lxb.rpc.protocol.message.RequestMessage;

/**
 * 跟踪工厂
 */
@Extensible("TraceFactory")
public interface TraceFactory {

    int ORDER_SKYWALKING = 100;
    int ORDER_PINPOINT = ORDER_SKYWALKING + 10;
    int ORDER_JAEGER = ORDER_PINPOINT + 10;
    int ORDER_ZIPKIN = ORDER_JAEGER + 10;

    /**
     * 构造跟踪会话
     *
     * @param request
     * @return
     */
    Tracer create(RequestMessage<Invocation> request);

}
