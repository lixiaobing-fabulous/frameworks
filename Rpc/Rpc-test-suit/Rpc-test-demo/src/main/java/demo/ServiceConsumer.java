package demo;


import com.lxb.rpc.client.RpcClient;

/**
 * Service Consumer
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public class ServiceConsumer {

    public static void main(String[] args) throws Exception {
        try (RpcClient rpcClient = new RpcClient()) {
            EchoService echoService = rpcClient.getService("echoService", EchoService.class);
            Thread.sleep(1000);
            System.out.println(echoService.echo("Hello,World"));
        }
        System.in.read();
    }
}
