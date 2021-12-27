package com.lxb.resilience;

import com.lxb.resilience.exception.CircuitBreakerException;
import com.lxb.resilience.test.CircuitBreakerTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.lxb.resilience")
public class Application {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        CircuitBreakerTest                 bean    = context.getBean(CircuitBreakerTest.class);
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);
                bean.test();
            } catch (CircuitBreakerException e) {
                e.printStackTrace();
            }
        }
    }
}
