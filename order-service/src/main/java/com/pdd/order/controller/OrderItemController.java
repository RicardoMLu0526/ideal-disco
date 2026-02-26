package com.pdd.order.controller;

import com.pdd.order.entity.OrderItem;
import com.pdd.order.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderItemController {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @GetMapping("/{orderId}/items")
    public Map<String, Object> getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        return Map.of("code", 200, "message", "success", "data", orderItems);
    }

    @GetMapping("/{orderId}/items/{id}")
    public Map<String, Object> getOrderItemDetail(@PathVariable Long orderId, @PathVariable Long id) {
        OrderItem orderItem = orderItemMapper.selectById(id);
        if (orderItem == null || !orderItem.getOrderId().equals(orderId)) {
            return Map.of("code", 404, "message", "订单项不存在");
        }
        return Map.of("code", 200, "message", "success", "data", orderItem);
    }
}
