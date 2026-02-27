```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\config\SentinelConfig.java
package com.pdd.order.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentinelConfig {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.config.SentinelConfigTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```