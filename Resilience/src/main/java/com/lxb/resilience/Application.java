package com.lxb.resilience;

import com.lxb.resilience.test.TestTimeout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.lxb.resilience")
public class Application {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);

        // 断路器
        //        CircuitBreakerTest                 bean    = context.getBean(CircuitBreakerTest.class);
        //        for (int i = 0; i < 20; i++) {
        //            try {
        //                Thread.sleep(1000);
        //                bean.test();
        //            } catch (CircuitBreakerException e) {
        //                e.printStackTrace();
        //            }
        //        }

        // 限流器
        //        RateLimitTest bean = context.getBean(RateLimitTest.class);
        //        for (int i = 0; i < 20; i++) {
        //            try {
        //                bean.rateLimit();
        //            } catch (CircuitBreakerException e) {
        //                e.printStackTrace();
        //            }
        //        }

        // 回退
        //        FallbackTest bean = context.getBean(FallbackTest.class);
        //        bean.fallbackTest();

        // 重试
        //        RetryTest bean = context.getBean(RetryTest.class);
        //        bean.retry();

        // 超时
                TestTimeout bean = context.getBean(TestTimeout.class);
                bean.timeout();
    }
}
