package com.pdd.order.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.pdd.order",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*config.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*actuator.*")
    }
)
public class TestConfiguration {
}
