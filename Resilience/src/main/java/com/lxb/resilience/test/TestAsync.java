package com.lxb.resilience.test;

import com.lxb.resilience.annotation.Asynchronous;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

import static java.lang.String.format;

@Component
public class TestAsync {

    @Asynchronous
    public Future<Void> echo(Object message) {
        System.out.println((format("[%s] - echo : %s", Thread.currentThread().getName(), message)));
        return null;
    }

}
