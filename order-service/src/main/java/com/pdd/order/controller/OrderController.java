package com.pdd.order.controller;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Map<String, Object> getOrderList() {
        List<Order> orders = orderService.getOrderList();
        return Map.of("code", 200, "message", "success", "data", orders);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOrderDetail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Map.of("code", 200, "message", "success", "data", orderVO);
    }

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody OrderCreateDTO createDTO) {
        Order order = orderService.createOrder(createDTO);
        return Map.of("code", 200, "message", "success", "data", Map.of("orderId", order.getId(), "orderNo", order.getOrderNo()));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        return Map.of("code", 200, "message", "success", "data", updatedOrder);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Map.of("code", 200, "message", "success");
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Map.of("code", 200, "message", "success");
    }

    @PostMapping("/{id}/pay")
    public Map<String, Object> payOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String paymentMethod = request.get("paymentMethod");
        orderService.payOrder(id, paymentMethod);
        return Map.of("code", 200, "message", "success", "data", Map.of("paymentUrl", "https://payment.example.com/pay?orderId=" + id));
    }

    @PostMapping("/{id}/confirm")
    public Map<String, Object> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Map.of("code", 200, "message", "success");
    }

    @PostMapping("/{id}/ship")
    public Map<String, Object> shipOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String trackingNumber = request.get("trackingNumber");
        orderService.shipOrder(id, trackingNumber);
        return Map.of("code", 200, "message", "success");
    }
}
