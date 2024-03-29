package com.lxb.rpc.cache;





import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.lxb.rpc.Plugin.CACHE;

/**
 * 缓存测试
 */
public class CacheTest {

    /**
     * 构建缓存
     *
     * @param type
     * @param name
     * @param expireAfterWrite
     * @return
     */
    protected Cache<String, String> buildCache(String type, String name, int expireAfterWrite) {
        CacheConfig.Builder<String, String> builder = CacheConfig.builder();
        CacheConfig<String, String> cacheConfig = builder.keyClass(String.class).valueClass(String.class).expireAfterWrite(expireAfterWrite).build();
        CacheFactory cacheFactory = CACHE.get(type);
        return cacheFactory.build(name, cacheConfig);
    }

    @Test
    public void testGetAndRemove() throws ExecutionException, InterruptedException {
        //获取缓存插件
        List<String> types = CACHE.names();
        //遍历缓存
        for (String type : types) {
            //构建缓存
            Cache<String, String> cache = buildCache(type, type, -1);
            cache.put("0", "0").get();
            Assertions.assertEquals(cache.get("0").get().getResult(), "0");
            cache.remove("0");
            Assertions.assertNull(cache.get("0").get());
        }
    }

    @Test
    public void testExpire() throws ExecutionException, InterruptedException {
        //获取缓存插件
        List<String> types = CACHE.names();
        CountDownLatch lath = new CountDownLatch(1);
        //遍历缓存
        for (String type : types) {
            //构建缓存
            Cache<String, String> cache = buildCache(type, type, 1000);
            cache.put("0", "0").get();
            Assertions.assertEquals(cache.get("0").get().getResult(), "0");
            lath.await(1000, TimeUnit.MILLISECONDS);
            Assertions.assertNull(cache.get("0").get());
        }
    }

    @Test
    public void testTps() throws ExecutionException, InterruptedException {
        final String[] keys = new String[5000];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = String.valueOf(i);
        }
        final Map<String, Cache<String, String>> caches = new HashMap<>();
        //获取缓存插件
        List<String> types = CACHE.names();
        //遍历缓存
        for (String type : types) {
            //构建缓存
            Cache<String, String> cache = buildCache(type, type, -1);
            for (String key : keys) {
                cache.put(key, key).get();
            }
            caches.put(type, cache);
        }

        int count = 1000000;
        int threads = 3;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Future<Long>[] futures = new Future[threads];

        //遍历缓存
        for (Map.Entry<String, Cache<String, String>> entry : caches.entrySet()) {
            Cache<String, String> cache = entry.getValue();
            //多线程读访问
            for (int i = 0; i < threads; i++) {
                futures[i] = service.submit(() -> {
                    long time = 0;
                    long startTime;
                    long endTime;
                    for (int j = 0; j < count; j++) {
                        startTime = System.nanoTime();
                        cache.get(keys[j % keys.length]);
                        endTime = System.nanoTime();
                        time += endTime - startTime;
                    }
                    return time;
                });
            }
            long time = 0;
            for (Future<Long> future : futures) {
                time += future.get();
            }

            System.out.println(String.format("%s tps %d in %d threads", entry.getKey(), count * threads * 1000000000L / time, threads));
        }

    }
}
