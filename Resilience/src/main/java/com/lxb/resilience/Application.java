package com.lxb.resilience;

import com.lxb.resilience.annotation.Retry;
import com.lxb.resilience.test.FallbackTest;
import com.lxb.resilience.test.RetryTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;

@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.lxb.resilience")
public class Application {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        RetryTest                          bean    = context.getBean(RetryTest.class);
        System.out.println(bean.retry());
    }
}
