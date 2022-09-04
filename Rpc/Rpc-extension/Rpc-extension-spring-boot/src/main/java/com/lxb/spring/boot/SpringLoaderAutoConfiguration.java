package com.lxb.spring.boot;


import com.lxb.extension.spring.SpringLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnMissingBean(SpringLoader.class)
@ConditionalOnProperty(prefix = "extension", name = "spring", havingValue = "true", matchIfMissing = true)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringLoaderAutoConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static SpringLoader springExtensionLoader() {
        return new SpringLoader();
    }
}
