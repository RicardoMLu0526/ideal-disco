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

F:\Program\service\order-service>gradle test --tests "com.pdd.order.config.SeataConfigTest"
Directory 'C:\java01' (Windows Registry) used for java installations does not exist
Directory 'C:\java01' (Windows Registry) used for java installations does not exist
> Task :order-service:test FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':order-service:test'.
> There were failing tests. See the report at: file:///F:/Program/service/order-service/build/reports/tests/test/index.html

* Try:
> Run with --scan to get full insights.

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.6/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD FAILED in 5s
5 actionable tasks: 1 executed, 4 up-to-date

F:\Program\service\order-service>cd ..

F:\Program\service>
```