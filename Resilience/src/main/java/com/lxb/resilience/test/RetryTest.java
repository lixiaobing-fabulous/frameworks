package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Retry;

@Component
public class RetryTest {
    @Retry(delay = 1000)
    public int retry() {
        System.out.println("execute...");
        return 1 / 0;
    }
}
