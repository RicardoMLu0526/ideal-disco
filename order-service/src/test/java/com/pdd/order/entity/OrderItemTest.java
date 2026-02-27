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
        // 准备测试数据
        OrderItem orderItem = new OrderItem();
        Long id = 1L;
        Long orderId = 1L;
        Long productId = 1L;
        Long productSkuId = 1L;
        String productName = "测试商品";
        String productSku = "规格1";
        BigDecimal price = new BigDecimal(100.00);
        Integer quantity = 2;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // 设置属性
        orderItem.setId(id);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        orderItem.setProductSkuId(productSkuId);
        orderItem.setProductName(productName);
        orderItem.setProductSku(productSku);
        orderItem.setPrice(price);
        orderItem.setQuantity(quantity);
        orderItem.setCreatedAt(createdAt);
        orderItem.setUpdatedAt(updatedAt);

        // 验证属性
        assertEquals(id, orderItem.getId());
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
        // 测试无参构造器
        OrderItem orderItem = new OrderItem();
        assertNotNull(orderItem);
    }

    @Test
    void testOrderItemTableAnnotation() {
        // 测试表名注解
        TableName tableNameAnnotation = OrderItem.class.getAnnotation(TableName.class);
        assertNotNull(tableNameAnnotation);
        assertEquals("order_item", tableNameAnnotation.value());
    }

    @Test
    void testOrderItemIdAnnotation() {
        // 测试ID注解
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
