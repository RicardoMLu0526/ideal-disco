package com.pdd.order.test;

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

@WebMvcTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    /**
     * 测试获取订单列表
     */
    @Test
    public void testGetOrderList() throws Exception {
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
        System.out.println("测试获取订单列表成功");
    }

    /**
     * 测试获取订单详情
     */
    @Test
    public void testGetOrderDetail() throws Exception {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(1L);
        orderVO.setOrderNo("20260226000001");

        Mockito.when(orderService.getOrderDetail(anyLong())).thenReturn(orderVO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1L));
        System.out.println("测试获取订单详情成功");
    }

    /**
     * 测试创建订单
     */
    @Test
    public void testCreateOrder() throws Exception {
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
        System.out.println("测试创建订单成功");
    }

    /**
     * 测试取消订单
     */
    @Test
    public void testCancelOrder() throws Exception {
        Mockito.doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        System.out.println("测试取消订单成功");
    }

    /**
     * 测试支付订单
     */
    @Test
    public void testPayOrder() throws Exception {
        Mockito.doNothing().when(orderService).payOrder(anyLong(), anyString());

        String json = "{\"paymentMethod\": \"wechat\"}";

        mockMvc.perform(post("/api/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        System.out.println("测试支付订单成功");
    }

    /**
     * 测试确认收货
     */
    @Test
    public void testConfirmOrder() throws Exception {
        Mockito.doNothing().when(orderService).confirmOrder(anyLong());

        mockMvc.perform(post("/api/orders/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        System.out.println("测试确认收货成功");
    }

    /**
     * 测试发货
     */
    @Test
    public void testShipOrder() throws Exception {
        Mockito.doNothing().when(orderService).shipOrder(anyLong(), anyString());

        String json = "{\"trackingNumber\": \"SF1234567890\"}";

        mockMvc.perform(post("/api/orders/1/ship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        System.out.println("测试发货成功");
    }
}
