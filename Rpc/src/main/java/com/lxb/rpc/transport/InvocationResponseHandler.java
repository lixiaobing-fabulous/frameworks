package com.lxb.rpc.transport;

import com.lxb.rpc.InvocationResponse;
import com.lxb.rpc.client.ExchangeFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.lxb.rpc.client.ExchangeFuture.removeExchangeFuture;

public class InvocationResponseHandler extends SimpleChannelInboundHandler<InvocationResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InvocationResponse response) throws Exception {
        String         requestId      = response.getRequestId();
        ExchangeFuture exchangeFuture = removeExchangeFuture(requestId);
        if (exchangeFuture != null) {
            Object result = response.getEntity();
            exchangeFuture.getPromise().setSuccess(result);
        }

    }
}
