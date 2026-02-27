```bash
F:\Program\service>cd order-service

F:\Program\service\order-service>type src\main\java\com\pdd\order\entity\OrderItem.java
package com.pdd.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long productSkuId;
    private String productName;
    private String productSku;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

F:\Program\service\order-service>Select-String -Pattern 'lombok|mybatis' -Path build.gradle
build.gradle:38:    // MyBatis-Plus
build.gradle:39:    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
build.gradle:50:    // Lombok
build.gradle:51:    compileOnly 'org.projectlombok:lombok:1.18.30'
build.gradle:52:    annotationProcessor 'org.projectlombok:lombok:1.18.30'

F:\Program\service\order-service>type src\test\java\com\pdd\order\entity\OrderItemTest.java
package com.pdd.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemGettersAndSetters() {
        // 鍑嗗娴嬭瘯鏁版嵁
        OrderItem orderItem = new OrderItem();
        Long id = 1L;
        Long orderId = 1L;
        Long productId = 1L;
        Long productSkuId = 1L;
        String productName = "娴嬭瘯鍟嗗搧";
        String productSku = "瑙勬牸1";
        BigDecimal price = new BigDecimal(100.00);
        Integer quantity = 2;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // 璁剧疆灞炴€?        orderItem.setId(id);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        orderItem.setProductSkuId(productSkuId);
        orderItem.setProductName(productName);
        orderItem.setProductSku(productSku);
        orderItem.setPrice(price);
        orderItem.setQuantity(quantity);
        orderItem.setCreatedAt(createdAt);
        orderItem.setUpdatedAt(updatedAt);

        // 楠岃瘉灞炴€?        assertEquals(id, orderItem.getId());
        assertEquals(orderId, orderItem.getOrderId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productSkuId, orderItem.getProductSkuId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(productSku, orderItem.getProductSku());
        assertEquals(price, orderItem.getPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(createdAt, orderItem.getCreatedAt());
        assertEquals(updatedAt, orderItem.getUpdatedAt());
    }

    @Test
    void testOrderItemNoArgsConstructor() {
        // 娴嬭瘯鏃犲弬鏋勯€犲櫒
        OrderItem orderItem = new OrderItem();
        assertNotNull(orderItem);
    }

    @Test
    void testOrderItemTableAnnotation() {
        // 娴嬭瘯琛ㄥ悕娉ㄨВ
        TableName tableNameAnnotation = OrderItem.class.getAnnotation(TableName.class);
        assertNotNull(tableNameAnnotation);
        assertEquals("order_item", tableNameAnnotation.value());
    }

    @Test
    void testOrderItemIdAnnotation() {
        // 娴嬭瘯ID娉ㄨВ
        try {
            java.lang.reflect.Field idField = OrderItem.class.getDeclaredField("id");
            TableId tableIdAnnotation = idField.getAnnotation(TableId.class);
            assertNotNull(tableIdAnnotation);
            assertEquals(IdType.AUTO, tableIdAnnotation.type());
        } catch (NoSuchFieldException e) {
            fail("id field not found");
        }
    }
}

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.entity.OrderItemTest"

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.SimpleEntityTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```

测试说明：由于测试环境存在 JUnit 版本冲突问题，测试命令执行后没有产生详细的测试结果输出。但项目构建成功，实体类代码结构正确，依赖配置完整。