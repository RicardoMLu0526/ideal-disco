package com.pdd.order.integration;

import com.pdd.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceUnitTest {

    private OrderService orderService = Mockito.mock(OrderService.class);

    @Test
    void testOrderServiceInjection() {
        // 只测试OrderService是否能够被创建
        assertNotNull(orderService, "OrderService should be created");
    }
}
