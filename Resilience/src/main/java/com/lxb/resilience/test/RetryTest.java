package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Retry;

@Component
public class RetryTest {
    @Retry(delay = 0)
    public int retry() {
        System.out.println("execute...");
        return 1 / 0;
    }
}
