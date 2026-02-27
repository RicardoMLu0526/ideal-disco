```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\entity\Order.java
package com.pdd.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private Integer paymentStatus;
    private Integer orderStatus;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime confirmTime;
    private LocalDateTime cancelTime;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

F:\Program\service>Select-String -Pattern 'lombok|mybatis-plus' -Path order-service\build.gradle
order-service\build.gradle:38:    // MyBatis-Plus
order-service\build.gradle:39:    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
order-service\build.gradle:50:    // Lombok
order-service\build.gradle:51:    compileOnly 'org.projectlombok:lombok:1.18.30'
order-service\build.gradle:52:    annotationProcessor 'org.projectlombok:lombok:1.18.30'

F:\Program\service>cd order-service

F:\Program\service\order-service>gradle test --tests "com.pdd.order.entity.OrderTest"
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

BUILD FAILED in 4s
5 actionable tasks: 1 executed, 4 up-to-date

F:\Program\service\order-service>cd ..

F:\Program\service>
```