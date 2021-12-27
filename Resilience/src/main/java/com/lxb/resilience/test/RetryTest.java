package com.lxb.resilience.test;

import com.lxb.resilience.annotation.Retry;
import org.springframework.stereotype.Component;

@Component
public class RetryTest {
    @Retry(delay = 0)
    public int retry() {
        System.out.println("execute...");
        return 1 / 0;
    }
}
