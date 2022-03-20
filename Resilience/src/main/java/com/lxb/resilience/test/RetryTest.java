package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Retry;
import com.lxb.resilience.annotation.Timeout;

@Component
public class RetryTest {
    @Retry(delay = 1000, maxRetries = 5)
    @Timeout
    public int retry() {
        System.out.println("execute...");
        return 1 / 0;
    }
}
