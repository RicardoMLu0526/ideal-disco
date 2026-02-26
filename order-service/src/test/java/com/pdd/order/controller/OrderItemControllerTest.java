package com.pdd.order.controller;

import com.pdd.order.entity.OrderItem;
import com.pdd.order.mapper.OrderItemMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemMapper orderItemMapper;

    @Test
    void testGetOrderItems() throws Exception {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(1L);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItems.add(orderItem);

        Mockito.when(orderItemMapper.selectByOrderId(anyLong())).thenReturn(orderItems);

        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void testGetOrderItemDetail() throws Exception {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(1L);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        Mockito.when(orderItemMapper.selectById(anyLong())).thenReturn(orderItem);

        mockMvc.perform(get("/api/orders/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void testGetOrderItemDetailNotFound() throws Exception {
        Mockito.when(orderItemMapper.selectById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/orders/1/items/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("订单项不存在"));
    }
}
