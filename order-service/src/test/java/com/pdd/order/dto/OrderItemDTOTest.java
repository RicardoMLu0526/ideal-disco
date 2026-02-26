package com.pdd.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemDTOTest {

    @Test
    void testOrderItemDTO() {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        
        assertNotNull(itemDTO.getProductId(), "Product ID should not be null");
        assertTrue(itemDTO.getQuantity() > 0, "Quantity should be positive");
    }
}
