package com.lxb.rpc.server;

import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.registry.Registry;
import com.lxb.rpc.codec.MessageDecoder;
import com.lxb.rpc.codec.MessageEncoder;
import com.lxb.rpc.context.ServiceContext;
import com.lxb.rpc.service.DefaultServiceInstance;
import com.lxb.rpc.service.ServiceInstance;
import com.lxb.rpc.service.ServiceRegistry;
import com.lxb.rpc.transport.InvocationRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.UUID;

import static com.lxb.rpc.Plugin.REGISTRY;

public class RpcServer implements AutoCloseable {

    private final String applicationName;

    private final int port;

    private final ServiceContext serviceContext;

    private final ServiceRegistry serviceRegistry;

    private final ServiceInstance localServiceInstance;

    private ServerBootstrap bootstrap;

    private EventLoopGroup group;

    private NioEventLoopGroup workerGroup;

    private Channel channel;

    public RpcServer(String applicationName, int port) {
        this.applicationName = applicationName;
        this.port = port;
        this.serviceContext = ServiceContext.DEFAULT;
        this.serviceRegistry = ServiceRegistry.DEFAULT;
        this.localServiceInstance = createLocalServiceInstance();
    }

    private ServiceInstance createLocalServiceInstance() {
        DefaultServiceInstance serviceInstance = new DefaultServiceInstance();
        serviceInstance.setId(UUID.randomUUID().toString());
        serviceInstance.setHost("127.0.0.1");
        serviceInstance.setPort(port);
        serviceInstance.setServiceName(applicationName);
        // TODO
        serviceInstance.setMetadata(new HashMap<>());
        return serviceInstance;
    }

    @SneakyThrows
    public RpcServer registerService(String serviceName, Object service) {
        serviceContext.registerService(serviceName, service);
        Registry registry = REGISTRY.get("consul").getRegistry(URL.valueOf("consul://0.0.0.0" + "?address=localhost"));
        registry.open().get();
        registry.register(URL.valueOf("joy://" + localServiceInstance.getHost() + ":" + localServiceInstance.getPort() +
                "?alias=" + serviceName + "&serviceName=" + serviceName + "&side=provider"));
        return this;
    }

    public RpcServer start() {
        this.bootstrap = new ServerBootstrap();
        this.group = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        bootstrap.group(group, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("message-encoder", new MessageEncoder());
                        ch.pipeline().addLast("message-decoder", new MessageDecoder());
                        ch.pipeline().addLast("request-handler", new InvocationRequestHandler(serviceContext));
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(port);
        // 注册服务
        registerServer();
        try {
            channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void registerServer() {
        serviceRegistry.register(localServiceInstance);
    }

    private void deregisterServer() {
        serviceRegistry.deregister(localServiceInstance);
    }

    @Override
    public void close() throws Exception {
        deregisterServer();
        if (channel != null) {
            channel.close().sync();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
