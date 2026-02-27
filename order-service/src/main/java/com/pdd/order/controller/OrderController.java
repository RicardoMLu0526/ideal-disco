package com.pdd.order.controller;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        return Map.of("code", 200, "message", "success", "data", orders, "timestamp", System.currentTimeMillis());
    }

    @GetMapping("/{orderNo}")
    public Map<String, Object> getOrderDetail(@PathVariable String orderNo) {
        OrderVO orderVO = orderService.getOrderDetailByOrderNo(orderNo);
        return Map.of("code", 200, "message", "success", "data", orderVO, "timestamp", System.currentTimeMillis());
    }

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody OrderCreateDTO createDTO) {
        Order order = orderService.createOrder(createDTO);
        return Map.of("code", 200, "message", "success", "data", Map.of("orderNo", order.getOrderNo(), "totalAmount", order.getTotalAmount(), "actualAmount", order.getActualAmount(), "status", order.getOrderStatus()), "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/{orderNo}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrderByOrderNo(orderNo);
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }

    @PutMapping("/{orderNo}/status")
    public Map<String, Object> updateOrderStatus(@PathVariable String orderNo, @RequestBody Map<String, Object> request) {
        Integer status = (Integer) request.get("status");
        Long paymentId = (Long) request.get("paymentId");
        String payTimeStr = (String) request.get("payTime");
        orderService.updateOrderStatus(orderNo, status, paymentId, LocalDateTime.parse(payTimeStr));
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/{orderNo}/paid")
    public Map<String, Object> orderPaidCallback(@PathVariable String orderNo, @RequestBody Map<String, Object> request) {
        String paymentNo = (String) request.get("paymentNo");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String payTimeStr = (String) request.get("payTime");
        Integer paymentMethod = (Integer) request.get("paymentMethod");
        orderService.handlePaymentSuccess(orderNo, paymentNo, amount, LocalDateTime.parse(payTimeStr), paymentMethod);
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/{orderNo}/payment-failed")
    public Map<String, Object> orderPaymentFailedCallback(@PathVariable String orderNo, @RequestBody Map<String, Object> request) {
        String paymentNo = (String) request.get("paymentNo");
        String failReason = (String) request.get("failReason");
        String failTimeStr = (String) request.get("failTime");
        orderService.handlePaymentFailed(orderNo, paymentNo, failReason, LocalDateTime.parse(failTimeStr));
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/{orderNo}/payment-timeout")
    public Map<String, Object> orderPaymentTimeoutCallback(@PathVariable String orderNo, @RequestBody Map<String, Object> request) {
        String paymentNo = (String) request.get("paymentNo");
        Integer timeoutMinutes = (Integer) request.get("timeoutMinutes");
        String timeoutTimeStr = (String) request.get("timeoutTime");
        orderService.handlePaymentTimeout(orderNo, paymentNo, timeoutMinutes, LocalDateTime.parse(timeoutTimeStr));
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/{orderNo}/receive")
    public Map<String, Object> confirmOrder(@PathVariable String orderNo) {
        orderService.confirmOrderByOrderNo(orderNo);
        return Map.of("code", 200, "message", "success", "timestamp", System.currentTimeMillis());
    }
}
