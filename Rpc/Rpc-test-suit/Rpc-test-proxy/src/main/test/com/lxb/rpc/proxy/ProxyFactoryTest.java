package com.lxb.rpc.proxy;






import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import static com.lxb.rpc.Plugin.PROXY;


/**
 */
public class ProxyFactoryTest {

    @Test
    public void testProxy() {
        List<String> types = PROXY.names();
        ProxyFactory factory;
        HelloService helloService;
        InvocationHandler handler = new MockProxyInvoker();
        for (String type : types) {
            factory = PROXY.get(type);
            helloService = factory.getProxy(HelloService.class, handler);
            helloService.sayHello("hello");
        }
    }

    @Test
    public void testTps() {

        List<String> types = PROXY.names();

        long startTime;
        long endTime;
        long totalTime;
        long count = 500000;
        ProxyFactory factory;
        HelloService helloService;
        InvocationHandler handler = new MockProxyInvoker();
        //性能测试
        for (String type : types) {
            factory = PROXY.get(type);
            totalTime = 0;
            helloService = factory.getProxy(HelloService.class, handler);
            for (int i = 0; i < count; i++) {
                startTime = System.nanoTime();
                helloService.sayHello("hello");
                endTime = System.nanoTime();
                totalTime += endTime - startTime;
            }
            System.out.println(String.format("%s time %d, tps %d", type, totalTime, (int) (1000000000.0 / totalTime * count)));
        }
    }

//    @Test
//    public void testGrpType() throws NoSuchMethodException, NoSuchFieldException {
//        Supplier<String> supplier = () -> String.valueOf(ThreadLocalRandom.current().nextInt(1000));
//        Method method = HelloService.class.getMethod("sayHello", String.class);
//        for (IDLFactory factory : Plugin.GRPC_FACTORY.extensions()) {
//            IDLMethodDesc type = factory.build(HelloService.class, method, supplier);
//            Class<?> clazz = type.getRequest().getClazz();
//            Field[] fields = clazz.getDeclaredFields();
//            Assertions.assertEquals(fields.length, 1);
//            clazz = type.getResponse().getClazz();
//            fields = clazz.getDeclaredFields();
//            Assertions.assertEquals(fields.length, 1);
//        }
//    }

    /**
     * The type Mock proxy invoker.
     */
    static class MockProxyInvoker implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "sayHello":
                    return args[0];
            }
            throw new IllegalArgumentException();
        }
    }
}
