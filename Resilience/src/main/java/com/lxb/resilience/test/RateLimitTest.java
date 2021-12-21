package com.lxb.resilience.test;

import com.lxb.resilience.annotation.RateLimit;
import org.springframework.stereotype.Component;

@Component
public class RateLimitTest {

    @RateLimit(1)
    public void rateLimit() throws InterruptedException {
        System.out.println("hello");
        Thread.sleep(1000);
    }
}
