package com.pdd.order.integration;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.util.IdGenerator;
import com.pdd.order.util.OrderNoGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoEntityUtilIntegrationTest {

    @Test
    void testDtoToEntityConversion() {
        // 创建订单DTO
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        // 添加订单项
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.getItems().add(itemDTO);

        // 测试工具类
        IdGenerator idGenerator = new IdGenerator();
        Long orderId = idGenerator.nextId();
        String orderNo = OrderNoGenerator.generate();

        // 转换为订单实体
        Order order = new Order();
        order.setId(orderId);
        order.setOrderNo(orderNo);
        order.setUserId(createDTO.getUserId());
        order.setReceiverName(createDTO.getReceiverName());
        order.setReceiverPhone(createDTO.getReceiverPhone());
        order.setShippingAddress(createDTO.getShippingAddress());
        order.setTotalAmount(BigDecimal.valueOf(100.00));
        order.setActualAmount(BigDecimal.valueOf(90.00));
        order.setPaymentStatus(0); // 未支付
        order.setOrderStatus(0); // 待支付
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 转换为订单项实体
        for (OrderItemDTO orderItemDTO : createDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(idGenerator.nextId());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(orderItemDTO.getProductId());
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(50.00));
            orderItem.setProductName("测试商品");
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setUpdatedAt(LocalDateTime.now());

            // 验证订单项
            assertNotNull(orderItem.getId(), "Order item ID should be generated");
            assertTrue(orderItem.getOrderId().equals(orderId), "Order item should belong to the order");
            assertTrue(orderItem.getProductId().equals(orderItemDTO.getProductId()), "Product ID should match");
            assertTrue(orderItem.getQuantity().equals(orderItemDTO.getQuantity()), "Quantity should match");
        }

        // 验证订单
        assertNotNull(order.getId(), "Order ID should be generated");
        assertNotNull(order.getOrderNo(), "Order number should be generated");
        assertTrue(order.getUserId().equals(createDTO.getUserId()), "User ID should match");
        assertTrue(order.getReceiverName().equals(createDTO.getReceiverName()), "Receiver name should match");
        assertTrue(order.getReceiverPhone().equals(createDTO.getReceiverPhone()), "Receiver phone should match");
        assertTrue(order.getShippingAddress().equals(createDTO.getShippingAddress()), "Shipping address should match");
    }

    @Test
    void testIdGenerator() {
        IdGenerator idGenerator = new IdGenerator();
        Long id1 = idGenerator.nextId();
        Long id2 = idGenerator.nextId();

        assertNotNull(id1, "ID should be generated");
        assertNotNull(id2, "ID should be generated");
        assertTrue(!id1.equals(id2), "IDs should be different");
    }

    @Test
    void testOrderNoGenerator() {
        String orderNo1 = OrderNoGenerator.generate();
        String orderNo2 = OrderNoGenerator.generate();

        assertNotNull(orderNo1, "Order number should be generated");
        assertNotNull(orderNo2, "Order number should be generated");
        assertTrue(!orderNo1.equals(orderNo2), "Order numbers should be different");
        assertTrue(orderNo1.length() == 20, "Order number should be 20 characters long");
        assertTrue(orderNo2.length() == 20, "Order number should be 20 characters long");
    }
}
