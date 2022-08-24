/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lxb.rpc.client;

import com.lxb.rpc.InvocationRequest;
import com.lxb.rpc.loadbalance.ServiceInstanceSelector;
import com.lxb.rpc.service.ServiceInstance;
import com.lxb.rpc.service.ServiceRegistry;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.lxb.rpc.client.ExchangeFuture.createExchangeFuture;
import static com.lxb.rpc.client.ExchangeFuture.removeExchangeFuture;

/**
 * 服务调用处理
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServiceInvocationHandler implements InvocationHandler {

    private String serviceName;

    private final RpcClient rpcClient;

    private final ServiceRegistry serviceRegistry;

    private final ServiceInstanceSelector selector;

    public ServiceInvocationHandler(String serviceName, RpcClient rpcClient) {
        this.serviceName = serviceName;
        this.rpcClient = rpcClient;
        this.serviceRegistry = rpcClient.getServiceRegistry();
        this.selector = rpcClient.getSelector();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isObjectDeclaredMethod(method)) {
            return handleObjectMethod(proxy, method, args);
        }

        InvocationRequest request = createRequest(method, args);

        return execute(request, proxy);
    }

    private Object execute(InvocationRequest request, Object proxy) {

        ServiceInstance serviceInstance = selectServiceProviderInstance();

        ChannelFuture channelFuture = rpcClient.connect(serviceInstance);

        sendRequest(request, channelFuture);

        ExchangeFuture exchangeFuture = createExchangeFuture(request);

        try {
            return exchangeFuture.get();
        } catch (Exception e) {
            removeExchangeFuture(request.getRequestId());
        }

        throw new IllegalStateException("Invocation failed!");
    }

    private void sendRequest(InvocationRequest request, ChannelFuture channelFuture) {
        channelFuture.channel().writeAndFlush(request);
    }

    private ServiceInstance selectServiceProviderInstance() {
        List<ServiceInstance> serviceInstances = serviceRegistry.getServiceInstances(serviceName);
        return selector.select(serviceInstances);
    }

    private InvocationRequest createRequest(Method method, Object[] args) {
        InvocationRequest request = new InvocationRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        // TODO
        request.setMetadata(new HashMap<>());
        return request;
    }

    private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "equals":
                break;
            case "hashCode":
                break;
            case "toString":
                break;
        }
        return null;
    }

    private boolean isObjectDeclaredMethod(Method method) {
        return Object.class == method.getDeclaringClass();
    }
}
