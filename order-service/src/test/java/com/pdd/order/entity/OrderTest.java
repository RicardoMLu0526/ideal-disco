package com.pdd.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderGettersAndSetters() {
        // 准备测试数据
        Order order = new Order();
        Long id = 1L;
        String orderNo = "20260227000001";
        Long userId = 1L;
        BigDecimal totalAmount = new BigDecimal(200.00);
        BigDecimal actualAmount = new BigDecimal(190.00);
        Integer paymentStatus = 1;
        Integer orderStatus = 2;
        String shippingAddress = "北京市朝阳区";
        String receiverName = "张三";
        String receiverPhone = "13800138000";
        LocalDateTime paymentTime = LocalDateTime.now();
        LocalDateTime shippingTime = LocalDateTime.now();
        LocalDateTime confirmTime = LocalDateTime.now();
        LocalDateTime cancelTime = LocalDateTime.now();
        String trackingNumber = "SF1234567890";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // 设置属性
        order.setId(id);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(actualAmount);
        order.setPaymentStatus(paymentStatus);
        order.setOrderStatus(orderStatus);
        order.setShippingAddress(shippingAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentTime(paymentTime);
        order.setShippingTime(shippingTime);
        order.setConfirmTime(confirmTime);
        order.setCancelTime(cancelTime);
        order.setTrackingNumber(trackingNumber);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

        // 验证属性
        assertEquals(id, order.getId());
        assertEquals(orderNo, order.getOrderNo());
        assertEquals(userId, order.getUserId());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(actualAmount, order.getActualAmount());
        assertEquals(paymentStatus, order.getPaymentStatus());
        assertEquals(orderStatus, order.getOrderStatus());
        assertEquals(shippingAddress, order.getShippingAddress());
        assertEquals(receiverName, order.getReceiverName());
        assertEquals(receiverPhone, order.getReceiverPhone());
        assertEquals(paymentTime, order.getPaymentTime());
        assertEquals(shippingTime, order.getShippingTime());
        assertEquals(confirmTime, order.getConfirmTime());
        assertEquals(cancelTime, order.getCancelTime());
        assertEquals(trackingNumber, order.getTrackingNumber());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    void testOrderNoArgsConstructor() {
        // 测试无参构造器
        Order order = new Order();
        assertNotNull(order);
    }

    @Test
    void testOrderTableAnnotation() {
        // 测试表名注解
        TableName tableNameAnnotation = Order.class.getAnnotation(TableName.class);
        assertNotNull(tableNameAnnotation);
        assertEquals("order", tableNameAnnotation.value());
    }

    @Test
    void testOrderIdAnnotation() {
        // 测试ID注解
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            TableId tableIdAnnotation = idField.getAnnotation(TableId.class);
            assertNotNull(tableIdAnnotation);
            assertEquals(IdType.AUTO, tableIdAnnotation.type());
        } catch (NoSuchFieldException e) {
            fail("id field not found");
        }
    }
}
