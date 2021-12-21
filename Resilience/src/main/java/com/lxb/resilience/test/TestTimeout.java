package com.lxb.resilience.test;

import com.lxb.resilience.annotation.Timeout;
import org.springframework.stereotype.Component;

@Component
public class TestTimeout {

    @Timeout
    public void timeout() throws InterruptedException {
        Thread.sleep(3000L);
    }
}
