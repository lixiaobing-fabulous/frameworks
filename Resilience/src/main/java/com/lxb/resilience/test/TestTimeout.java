package com.lxb.resilience.test;

import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Timeout;

@Component
public class TestTimeout {

    @Timeout
    public void timeout() throws InterruptedException {
        Thread.sleep(3000L);
    }
}
