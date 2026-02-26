package com.pdd.order.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeataConfig {

    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        return new GlobalTransactionScanner("order-service", "order-service-group");
    }
}
