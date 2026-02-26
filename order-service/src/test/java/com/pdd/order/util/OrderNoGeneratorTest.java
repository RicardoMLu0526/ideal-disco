package com.pdd.order.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderNoGeneratorTest {

    @Test
    void testGenerate() {
        String orderNo = OrderNoGenerator.generate();
        assertNotNull(orderNo, "Generated order number should not be null");
        assertTrue(orderNo.length() > 0, "Generated order number should not be empty");
        
        // 测试订单号唯一性
        String orderNo2 = OrderNoGenerator.generate();
        assertTrue(!orderNo.equals(orderNo2), "Generated order numbers should be unique");
        
        // 测试订单号格式（年月日时分秒 + 6位随机数）
        assertTrue(orderNo.length() >= 14, "Order number should be at least 14 characters long");
    }
}
