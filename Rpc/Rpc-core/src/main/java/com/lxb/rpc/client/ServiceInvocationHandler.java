package com.lxb.rpc.client;

import com.lxb.extension.URL;
import com.lxb.rpc.InvocationRequest;
import com.lxb.rpc.cluster.Shard;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.naming.ClusterHandler;
import com.lxb.rpc.cluster.discovery.registry.Registry;
import com.lxb.rpc.loadbalance.ServiceInstanceSelector;
import com.lxb.rpc.service.ServiceInstance;
import com.lxb.rpc.service.ServiceRegistry;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.lxb.rpc.Plugin.REGISTRY;
import static com.lxb.rpc.client.ExchangeFuture.createExchangeFuture;
import static com.lxb.rpc.client.ExchangeFuture.removeExchangeFuture;

/**
 * 服务调用处理
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public class ServiceInvocationHandler implements InvocationHandler {
    private List<Shard> shards;
    private String serviceName;

    private final RpcClient rpcClient;

    private final ServiceRegistry serviceRegistry;

    private final ServiceInstanceSelector selector;

    public ServiceInvocationHandler(String serviceName, RpcClient rpcClient, Class serviceInterfaceClass) {
        this.serviceName = serviceName;
        this.rpcClient = rpcClient;
        this.serviceRegistry = rpcClient.getServiceRegistry();
        this.selector = rpcClient.getSelector();
        this.shards = new CopyOnWriteArrayList<>();
        URL url = URL.valueOf("consul://0.0.0.0" + "?alias=" + serviceInterfaceClass.getName() + "&serviceName=" + serviceInterfaceClass.getName() +
                "&address=localhost");
        Registry registry = REGISTRY.get("consul").getRegistry(url);

        registry.open();
        registry.subscribe(url, (ClusterHandler) event -> {
            List<ClusterEvent.ShardEvent> datum = event.getDatum();
            for (ClusterEvent.ShardEvent shardEvent : datum) {
                switch (shardEvent.getType()) {
                    case ADD:
                        Shard shardAdd = shardEvent.getShard();
                        shards.add(shardAdd);
                        break;
                    case DELETE:
                        Shard shardDelete = shardEvent.getShard();
                        shards.remove(shardDelete);
                        break;
                    case UPDATE:
                        Shard shardUpdate = shardEvent.getShard();
                        shards.remove(shardUpdate);
                        shards.add(shardUpdate);
                        break;
                }
            }
        });

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

        Shard serviceInstance = selectServiceProviderInstance(request);

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

    private Shard selectServiceProviderInstance(InvocationRequest request) {

        return selector.select(shards);
    }

    private InvocationRequest createRequest(Method method, Object[] args) {
        InvocationRequest request = new InvocationRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
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
