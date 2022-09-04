package com.lxb.rpc.client;

import com.lxb.extension.ExtensionManager;
import com.lxb.rpc.codec.MessageDecoder;
import com.lxb.rpc.codec.MessageEncoder;
import com.lxb.rpc.loadbalance.ServiceInstanceSelector;
import com.lxb.rpc.proxy.ProxyFactory;
import com.lxb.rpc.service.ServiceInstance;
import com.lxb.rpc.service.ServiceRegistry;
import com.lxb.rpc.transport.InvocationResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient implements AutoCloseable {

    private final ServiceRegistry serviceRegistry;

    private final ServiceInstanceSelector selector;

    private final Bootstrap bootstrap;

    private final EventLoopGroup group;

    public RpcClient(ServiceRegistry serviceRegistry, ServiceInstanceSelector selector) {
        this.serviceRegistry = serviceRegistry;
        this.selector = selector;
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("message-encoder", new MessageEncoder());
                        ch.pipeline().addLast("message-decoder", new MessageDecoder());
                        ch.pipeline().addLast("response-handler", new InvocationResponseHandler());
                    }
                });
    }

    public RpcClient() {
        this(ServiceRegistry.DEFAULT, ServiceInstanceSelector.DEFAULT);
    }

    public <T> T getService(String serviceName, Class<T> serviceInterfaceClass) {
        ProxyFactory proxyFactory = ExtensionManager.getOrLoadExtension(ProxyFactory.class);
        return (T) proxyFactory.getProxy(serviceInterfaceClass, new ServiceInvocationHandler(serviceName, this));
    }

    public ChannelFuture connect(ServiceInstance serviceInstance) {
        String        host          = serviceInstance.getHost();
        int           port          = serviceInstance.getPort();
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        return channelFuture.awaitUninterruptibly();
    }

    protected ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    protected ServiceInstanceSelector getSelector() {
        return selector;
    }

    protected Bootstrap getBootstrap() {
        return bootstrap;
    }

    @Override
    public void close() throws Exception {
        group.shutdownGracefully();
    }
}