package com.lxb.resilience;

import com.lxb.resilience.test.RateLimitTest;
import com.lxb.resilience.test.TestAsync;
import com.lxb.resilience.test.TestTimeout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.lxb.resilience")
public class Application {
    public static void main(String[] args) throws InterruptedException, IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        RateLimitTest                          bean    = context.getBean(RateLimitTest.class);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    bean.rateLimit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        System.in.read();
    }
}
