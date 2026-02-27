package com.pdd.order.integration;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestConfig.class, properties = "spring.profiles.active=test")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateOrderAndGetDetail() {
        // 创建订单
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.getItems().add(itemDTO);

        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "Order should be created");
        assertNotNull(order.getId(), "Order ID should be set");
        assertNotNull(order.getOrderNo(), "Order number should be set");

        // 获取订单详情
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "Order detail should be retrieved");
        assertTrue(orderVO.getId().equals(order.getId()), "Order ID should match");
        assertTrue(orderVO.getOrderNo().equals(order.getOrderNo()), "Order number should match");
    }

    @Test
    void testCancelOrder() {
        // 创建订单
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(1);
        createDTO.getItems().add(itemDTO);

        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "Order should be created");

        // 取消订单
        orderService.cancelOrder(order.getId());

        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "Order detail should be retrieved");
        // 注意：实际测试中需要根据具体的状态枚举值进行验证
    }
}
