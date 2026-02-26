package com.pdd.order.controller;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testGetOrderList() throws Exception {
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20260226000001");
        orders.add(order);

        Mockito.when(orderService.getOrderList()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void testGetOrderDetail() throws Exception {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(1L);
        orderVO.setOrderNo("20260226000001");

        Mockito.when(orderService.getOrderDetail(anyLong())).thenReturn(orderVO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20260226000001");

        Mockito.when(orderService.createOrder(any(OrderCreateDTO.class))).thenReturn(order);

        String json = "{\"userId\": 1, \"receiverName\": \"张三\", \"receiverPhone\": \"13800138000\", \"shippingAddress\": \"北京市朝阳区\", \"items\": [{\"productId\": 1, \"quantity\": 2}]}";

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.orderId").value(1L));
    }

    @Test
    void testCancelOrder() throws Exception {
        Mockito.doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void testPayOrder() throws Exception {
        Mockito.doNothing().when(orderService).payOrder(anyLong(), anyString());

        String json = "{\"paymentMethod\": \"wechat\"}";

        mockMvc.perform(post("/api/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }
}
