package com.lxb.resilience.test;

import com.lxb.resilience.annotation.Fallback;
import com.lxb.resilience.annotation.RateLimit;
import org.springframework.stereotype.Component;

@Component
public class FallbackTest {

    @Fallback(fallbackMethod = "fallback", fallbackFor = IllegalArgumentException.class, skipFor = IllegalStateException.class)
    public int fallbackTest() throws InterruptedException {
        return 1 / 0;
    }

    public int fallback() throws InterruptedException {
        System.out.println("fallback method");
        return 0;
    }


}
