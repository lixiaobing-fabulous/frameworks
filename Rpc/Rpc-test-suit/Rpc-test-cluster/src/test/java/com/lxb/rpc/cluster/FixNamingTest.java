package com.lxb.rpc.cluster;

import com.lxb.extension.URL;
import com.lxb.rpc.cluster.discovery.event.ClusterEvent;
import com.lxb.rpc.cluster.discovery.naming.ClusterHandler;
import com.lxb.rpc.cluster.discovery.naming.fix.FixRegistar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FixNamingTest {

    @Test
    public void test() throws InterruptedException {
        CountDownLatch   latch    = new CountDownLatch(2);
        MyClusterHandler handler1 = new MyClusterHandler(latch);
        MyClusterHandler handler2 = new MyClusterHandler(latch);
        URL         url      = URL.valueOf("joy://topic1");
        FixRegistar registar = new FixRegistar(URL.valueOf("joy://test?address=192.168.1.1,192.168.1.6?dataCenter=test&region=test"));
        registar.open().whenComplete((v, t) -> {
            Assertions.assertTrue(registar.subscribe(url, handler1));
            Assertions.assertFalse(registar.subscribe(url, handler1));
            Assertions.assertTrue(registar.subscribe(url, handler2));
        });
        latch.await();
        Assertions.assertEquals(handler1.count, 1);
        Assertions.assertEquals(handler1.shards.size(), 2);
        Assertions.assertEquals(handler2.count, 1);
        Assertions.assertEquals(handler2.shards.size(), 2);
    }

    protected static class MyClusterHandler implements ClusterHandler {

        protected int            count;
        protected List<Shard>    shards = new LinkedList<>();
        protected CountDownLatch latch;

        public MyClusterHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void handle(final ClusterEvent event) {
            List<ClusterEvent.ShardEvent> events = event.getDatum();
            if (events != null) {
                for (ClusterEvent.ShardEvent e : events) {
                    switch (e.getType()) {
                        case ADD:
                            shards.add(e.getShard());
                    }
                }
            }
            if (++count == 1) {
                latch.countDown();
            }

        }
    }
}
