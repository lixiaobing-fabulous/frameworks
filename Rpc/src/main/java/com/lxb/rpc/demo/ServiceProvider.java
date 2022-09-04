package com.lxb.rpc.demo;


import com.lxb.rpc.server.RpcServer;

/**
 * @author lixiaobing
 * @since 1.0.0
 */
public class ServiceProvider {

    public static void main(String[] args) throws Exception {
        try (RpcServer serviceServer = new RpcServer("echoService", 12345)) {
            serviceServer.registerService(EchoService.class.getName(), new DefaultEchoService());
            serviceServer.start();
        }
    }
}
