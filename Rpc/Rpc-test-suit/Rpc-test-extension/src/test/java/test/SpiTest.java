package test;

import api.Consumer;
import api.Filter;
import api.Producer;
import com.lxb.extension.ExtensionManager;
import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.extension.ExtensionSelector;
import com.lxb.extension.Ordered;
import com.lxb.extension.Selector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class SpiTest {
    @Test
    public void testGetOrLoadSpi() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        ExtensionPoint spi2 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        Assertions.assertNotNull(spi1);
        Assertions.assertEquals(spi1, spi2);
    }

    @Test
    public void testGetOrLoad() {
        Consumer c1 = ExtensionManager.getOrLoadExtension(Consumer.class);
        Consumer c2 = ExtensionManager.getOrLoadExtension(Consumer.class);
        Assertions.assertNotNull(c1);
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void testGet() {
        ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        Consumer c1 = ExtensionManager.getExtension(Consumer.class, "myConsumer@test");
        Consumer c2 = ExtensionManager.getExtension(Consumer.class, "myConsumer");
        Consumer c3 = ExtensionManager.getExtension("consumer", "myConsumer@test");
        Assertions.assertNotNull(c1);
        Assertions.assertEquals(c1, c2);
        Assertions.assertEquals(c1, c3);
    }

    @Test
    public void testSelector() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        ExtensionSelector<Consumer, String, Integer, Consumer> selector = new ExtensionSelector(spi1, new Selector.MatchSelector<Consumer, String, Integer>() {
            @Override
            protected boolean match(Consumer target, Integer condition) {
                return target.order() == condition;
            }
        });
        Consumer c1 = ExtensionManager.getExtension(Consumer.class, "myConsumer");
        Assertions.assertEquals(c1, selector.select(Ordered.ORDER));
    }

    @Test
    public void testPrototype() {
        Producer producer1 = ExtensionManager.getOrLoadExtension(Producer.class, "producer.MyProducer");
        Assertions.assertNotNull(producer1);
        Producer producer2 = ExtensionManager.getOrLoadExtension(Producer.class, "producer.MyProducer");
        Assertions.assertNotEquals(producer1, producer2);
    }

    @Test
    public void testLazy() {
        ExtensionPoint<Consumer, String> sp1 = new ExtensionPointLazy(Consumer.class);
        ExtensionPoint<Consumer, String> sp2 = new ExtensionPointLazy(Consumer.class);
        Consumer c1 = sp1.get();
        Consumer c2 = sp2.get();
        Assertions.assertNotNull(c1);
        Assertions.assertEquals(c1, sp1.get());
        Assertions.assertEquals(c1, c2);
        //测试单例缓存是否生效
        Assertions.assertEquals(sp1.extensions(), sp2.extensions());
    }

    @Test
    public void testListSelector() {
        ExtensionSelector<Filter, String, Filter.FilterType, List<Filter>> selector = new ExtensionSelector<Filter, String, Filter.FilterType, List<Filter>>(
                new ExtensionPointLazy<>(Filter.class),
                new Selector.CacheSelector<>(
                        new Selector.ListSelector<Filter, String, Filter.FilterType>() {
                            @Override
                            protected boolean match(Filter target, Filter.FilterType condition) {
                                return condition == Filter.FilterType.CONSUMER ? target.isConsumer() : !target.isConsumer();
                            }
                        }));
        List<Filter> consumers = selector.select(Filter.FilterType.CONSUMER);
        List<Filter> procedures = selector.select(Filter.FilterType.PROCEDURE);
        Assertions.assertEquals(2, consumers.size());
        Assertions.assertEquals(1, procedures.size());
    }

    @Test
    public void testDisable() {
        ExtensionPoint<Filter, String> point = ExtensionManager.getOrLoadExtensionPoint(Filter.class);
        Filter filter = point.get("filter1");
        Assertions.assertNotNull(filter);
        filter = point.get("filter3");
        Assertions.assertNull(filter);
        filter = point.get("filter4");
        Assertions.assertNull(filter);
    }
}
