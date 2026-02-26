package com.pdd.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderCreateDTOTest {

    @Test
    void testOrderCreateDTO() {
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        // 测试设置订单项
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.getItems().add(itemDTO);
        
        assertNotNull(createDTO.getUserId(), "User ID should not be null");
        assertNotNull(createDTO.getReceiverName(), "Receiver name should not be null");
        assertNotNull(createDTO.getReceiverPhone(), "Receiver phone should not be null");
        assertNotNull(createDTO.getShippingAddress(), "Shipping address should not be null");
        assertTrue(createDTO.getItems().size() > 0, "Items list should not be empty");
    }
}
