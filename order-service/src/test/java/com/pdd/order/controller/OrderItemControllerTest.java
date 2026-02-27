package com.pdd.order.controller;

import com.pdd.order.entity.OrderItem;
import com.pdd.order.mapper.OrderItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderItemControllerTest {

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemController orderItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrderItems() {
        // 准备测试数据
        Long orderId = 1L;
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(1L);
        orderItem.setProductName("测试商品");
        orderItem.setPrice(new BigDecimal(100.00));
        orderItem.setQuantity(2);
        orderItems.add(orderItem);

        // 模拟Mapper方法
        when(orderItemMapper.selectByOrderId(orderId)).thenReturn(orderItems);

        // 调用控制器方法
        Map<String, Object> result = orderItemController.getOrderItems(orderId);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertEquals(orderItems, result.get("data"));
        verify(orderItemMapper, times(1)).selectByOrderId(orderId);
    }

    @Test
    void testGetOrderItemDetail() {
        // 准备测试数据
        Long orderId = 1L;
        Long itemId = 1L;
        OrderItem orderItem = new OrderItem();
        orderItem.setId(itemId);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(1L);
        orderItem.setProductName("测试商品");
        orderItem.setPrice(new BigDecimal(100.00));
        orderItem.setQuantity(2);

        // 模拟Mapper方法
        when(orderItemMapper.selectById(itemId)).thenReturn(orderItem);

        // 调用控制器方法
        Map<String, Object> result = orderItemController.getOrderItemDetail(orderId, itemId);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertEquals(orderItem, result.get("data"));
        verify(orderItemMapper, times(1)).selectById(itemId);
    }

    @Test
    void testGetOrderItemDetail_NotFound() {
        // 准备测试数据
        Long orderId = 1L;
        Long itemId = 1L;

        // 模拟Mapper方法 - 返回null
        when(orderItemMapper.selectById(itemId)).thenReturn(null);

        // 调用控制器方法
        Map<String, Object> result = orderItemController.getOrderItemDetail(orderId, itemId);

        // 验证结果
        assertEquals(404, result.get("code"));
        assertEquals("订单项不存在", result.get("message"));
        verify(orderItemMapper, times(1)).selectById(itemId);
    }

    @Test
    void testGetOrderItemDetail_WrongOrderId() {
        // 准备测试数据
        Long orderId = 1L;
        Long wrongOrderId = 2L;
        Long itemId = 1L;
        OrderItem orderItem = new OrderItem();
        orderItem.setId(itemId);
        orderItem.setOrderId(wrongOrderId); // 订单项属于不同的订单
        orderItem.setProductId(1L);
        orderItem.setProductName("测试商品");

        // 模拟Mapper方法
        when(orderItemMapper.selectById(itemId)).thenReturn(orderItem);

        // 调用控制器方法
        Map<String, Object> result = orderItemController.getOrderItemDetail(orderId, itemId);

        // 验证结果
        assertEquals(404, result.get("code"));
        assertEquals("订单项不存在", result.get("message"));
        verify(orderItemMapper, times(1)).selectById(itemId);
    }
}
