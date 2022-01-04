package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.RateLimit;

@Component
public class RateLimitTest {

    @RateLimit(1)
    public void rateLimit() throws InterruptedException {
        System.out.println("hello");
        Thread.sleep(1000);
    }
}
