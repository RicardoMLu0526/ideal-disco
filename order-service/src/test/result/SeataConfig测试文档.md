```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\config\SeataConfig.java
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

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.config.SeataConfigTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```