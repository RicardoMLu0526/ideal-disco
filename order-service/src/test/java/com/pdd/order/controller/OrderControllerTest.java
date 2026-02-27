package com.pdd.order.controller;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrderList() {
        // 准备测试数据
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20260227000001");
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal(200.00));
        order.setActualAmount(new BigDecimal(200.00));
        orders.add(order);

        // 模拟服务层方法
        when(orderService.getOrderList()).thenReturn(orders);

        // 调用控制器方法
        Map<String, Object> result = orderController.getOrderList();

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertEquals(orders, result.get("data"));
        verify(orderService, times(1)).getOrderList();
    }

    @Test
    void testGetOrderDetail() {
        // 准备测试数据
        String orderNo = "20260227000001";
        OrderVO orderVO = new OrderVO();
        orderVO.setId(1L);
        orderVO.setOrderNo(orderNo);

        // 模拟服务层方法
        when(orderService.getOrderDetailByOrderNo(orderNo)).thenReturn(orderVO);

        // 调用控制器方法
        Map<String, Object> result = orderController.getOrderDetail(orderNo);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        assertEquals(orderVO, result.get("data"));
        verify(orderService, times(1)).getOrderDetailByOrderNo(orderNo);
    }

    @Test
    void testCreateOrder() {
        // 准备测试数据
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20260227000001");
        order.setTotalAmount(new BigDecimal(200));
        order.setActualAmount(new BigDecimal(200));
        order.setOrderStatus(0);

        // 模拟服务层方法
        when(orderService.createOrder(createDTO)).thenReturn(order);

        // 调用控制器方法
        Map<String, Object> result = orderController.createOrder(createDTO);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(order.getOrderNo(), data.get("orderNo"));
        assertEquals(order.getTotalAmount(), data.get("totalAmount"));
        assertEquals(order.getActualAmount(), data.get("actualAmount"));
        assertEquals(order.getOrderStatus(), data.get("status"));
        verify(orderService, times(1)).createOrder(createDTO);
    }

    @Test
    void testCancelOrder() {
        // 准备测试数据
        String orderNo = "20260227000001";

        // 模拟服务层方法
        doNothing().when(orderService).cancelOrderByOrderNo(orderNo);

        // 调用控制器方法
        Map<String, Object> result = orderController.cancelOrder(orderNo);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).cancelOrderByOrderNo(orderNo);
    }

    @Test
    void testUpdateOrderStatus() {
        // 准备测试数据
        String orderNo = "20260227000001";
        Map<String, Object> request = Map.of(
                "status", 1,
                "paymentId", 1L,
                "payTime", "2026-02-27T10:00:00"
        );

        // 模拟服务层方法
        doNothing().when(orderService).updateOrderStatus(orderNo, 1, 1L, LocalDateTime.parse("2026-02-27T10:00:00"));

        // 调用控制器方法
        Map<String, Object> result = orderController.updateOrderStatus(orderNo, request);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).updateOrderStatus(orderNo, 1, 1L, LocalDateTime.parse("2026-02-27T10:00:00"));
    }

    @Test
    void testOrderPaidCallback() {
        // 准备测试数据
        String orderNo = "20260227000001";
        Map<String, Object> request = Map.of(
                "paymentNo", "PAY20260227000001",
                "amount", 200.00,
                "payTime", "2026-02-27T10:00:00",
                "paymentMethod", 1
        );

        // 模拟服务层方法
        doNothing().when(orderService).handlePaymentSuccess(eq(orderNo), eq("PAY20260227000001"), any(BigDecimal.class), eq(LocalDateTime.parse("2026-02-27T10:00:00")), eq(1));

        // 调用控制器方法
        Map<String, Object> result = orderController.orderPaidCallback(orderNo, request);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).handlePaymentSuccess(eq(orderNo), eq("PAY20260227000001"), any(BigDecimal.class), eq(LocalDateTime.parse("2026-02-27T10:00:00")), eq(1));
    }

    @Test
    void testOrderPaymentFailedCallback() {
        // 准备测试数据
        String orderNo = "20260227000001";
        Map<String, Object> request = Map.of(
                "paymentNo", "PAY20260227000001",
                "failReason", "用户取消支付",
                "failTime", "2026-02-27T10:00:00"
        );

        // 模拟服务层方法
        doNothing().when(orderService).handlePaymentFailed(orderNo, "PAY20260227000001", "用户取消支付", LocalDateTime.parse("2026-02-27T10:00:00"));

        // 调用控制器方法
        Map<String, Object> result = orderController.orderPaymentFailedCallback(orderNo, request);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).handlePaymentFailed(orderNo, "PAY20260227000001", "用户取消支付", LocalDateTime.parse("2026-02-27T10:00:00"));
    }

    @Test
    void testOrderPaymentTimeoutCallback() {
        // 准备测试数据
        String orderNo = "20260227000001";
        Map<String, Object> request = Map.of(
                "paymentNo", "PAY20260227000001",
                "timeoutMinutes", 30,
                "timeoutTime", "2026-02-27T10:00:00"
        );

        // 模拟服务层方法
        doNothing().when(orderService).handlePaymentTimeout(orderNo, "PAY20260227000001", 30, LocalDateTime.parse("2026-02-27T10:00:00"));

        // 调用控制器方法
        Map<String, Object> result = orderController.orderPaymentTimeoutCallback(orderNo, request);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).handlePaymentTimeout(orderNo, "PAY20260227000001", 30, LocalDateTime.parse("2026-02-27T10:00:00"));
    }

    @Test
    void testConfirmOrder() {
        // 准备测试数据
        String orderNo = "20260227000001";

        // 模拟服务层方法
        doNothing().when(orderService).confirmOrderByOrderNo(orderNo);

        // 调用控制器方法
        Map<String, Object> result = orderController.confirmOrder(orderNo);

        // 验证结果
        assertEquals(200, result.get("code"));
        assertEquals("success", result.get("message"));
        verify(orderService, times(1)).confirmOrderByOrderNo(orderNo);
    }
}
