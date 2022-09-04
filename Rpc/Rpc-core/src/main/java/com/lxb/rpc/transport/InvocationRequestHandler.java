package com.lxb.rpc.transport;

import com.lxb.rpc.InvocationRequest;
import com.lxb.rpc.InvocationResponse;
import com.lxb.rpc.context.ServiceContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.reflect.MethodUtils;

import java.util.Arrays;

@Slf4j
public class InvocationRequestHandler extends SimpleChannelInboundHandler<InvocationRequest> {

    private final ServiceContext serviceContext;

    public InvocationRequestHandler(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvocationRequest request) throws Exception {
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class[] parameterTypes = request.getParameterTypes();

        Object service = serviceContext.getService(serviceName);
        Object entity = null;
        String errorMessage = null;
        try {
            entity = MethodUtils.invokeMethod(service, methodName, parameters, parameterTypes);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        log.info("Read {} and invoke the {}'s method[name:{}, param-types:{}, params:{}] : {}",
                request, serviceName, methodName, Arrays.asList(parameterTypes), Arrays.asList(parameters), entity);

        InvocationResponse response = new InvocationResponse();
        response.setRequestId(request.getRequestId());
        response.setEntity(entity);
        response.setErrorMessage(errorMessage);

        ctx.writeAndFlush(response);

        log.info("Write and Flush {}", response);
    }
}
