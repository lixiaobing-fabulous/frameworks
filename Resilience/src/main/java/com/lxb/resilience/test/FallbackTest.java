package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Fallback;

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
