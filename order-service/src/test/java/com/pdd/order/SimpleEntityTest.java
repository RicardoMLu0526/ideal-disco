package com.pdd.order;

import com.pdd.order.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEntityTest {
    
    @Test
    void testOrderItemCreation() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(1L);
        orderItem.setProductId(1L);
        orderItem.setProductName("Test Product");
        orderItem.setPrice(new BigDecimal(100.00));
        orderItem.setQuantity(2);
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());
        
        assertNotNull(orderItem);
        assertEquals(1L, orderItem.getId());
        assertEquals("Test Product", orderItem.getProductName());
    }
}
