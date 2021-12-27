package com.lxb.resilience.test;


import com.lxb.resilience.annotation.CircuitBreaker;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CircuitBreakerTest {
    Random random = new Random();

    @CircuitBreaker(maxVolume = 10, delay = 3000)
    @SneakyThrows
    public void test() {
        double v = random.nextDouble();
        System.out.println(v);
        if (v > 0.3) {
            throw new IllegalStateException();
        }
    }
}
