package com.pdd.order.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.pdd.order",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*config.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*actuator.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*feign.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*mapper.*")
    }
)
public class TestConfig {
    
    // 添加必要的测试Bean
}
