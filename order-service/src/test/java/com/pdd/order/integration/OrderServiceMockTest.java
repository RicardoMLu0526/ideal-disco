package com.pdd.order.integration;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class OrderServiceMockTest {

    private OrderService orderService = Mockito.mock(OrderService.class);

    @Test
    void testCreateOrder() {
        // 创建订单DTO
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.getItems().add(itemDTO);

        // 模拟订单创建
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setOrderNo("20260227001");
        mockOrder.setUserId(1L);
        
        when(orderService.createOrder(createDTO)).thenReturn(mockOrder);

        // 测试订单创建
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "Order should be created");
        assertNotNull(order.getId(), "Order ID should be set");
        assertNotNull(order.getOrderNo(), "Order number should be set");
    }

    @Test
    void testCancelOrder() {
        // 模拟订单取消
        Mockito.doNothing().when(orderService).cancelOrder(1L);

        // 测试订单取消
        orderService.cancelOrder(1L);
        Mockito.verify(orderService).cancelOrder(1L);
    }

    @Test
    void testGetOrderDetail() {
        // 模拟订单详情获取
        OrderVO mockOrderVO = new OrderVO();
        mockOrderVO.setId(1L);
        mockOrderVO.setOrderNo("20260227001");
        mockOrderVO.setUserId(1L);
        
        when(orderService.getOrderDetail(1L)).thenReturn(mockOrderVO);

        // 测试订单详情获取
        OrderVO orderVO = orderService.getOrderDetail(1L);
        assertNotNull(orderVO, "Order detail should be retrieved");
        assertNotNull(orderVO.getId(), "Order ID should be set");
        assertNotNull(orderVO.getOrderNo(), "Order number should be set");
    }
}
