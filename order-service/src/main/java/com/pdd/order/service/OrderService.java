package com.pdd.order.service;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.vo.OrderVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderVO getOrderDetail(Long orderId);
    OrderVO getOrderDetailByOrderNo(String orderNo);
    List<Order> getOrderList();
    Order createOrder(OrderCreateDTO createDTO);
    Order updateOrder(Long orderId, Order order);
    void deleteOrder(Long orderId);
    void cancelOrder(Long orderId);
    void cancelOrderByOrderNo(String orderNo);
    void payOrder(Long orderId, String paymentMethod);
    void confirmOrder(Long orderId);
    void confirmOrderByOrderNo(String orderNo);
    void shipOrder(Long orderId, String trackingNumber);
    void updateOrderStatus(String orderNo, Integer status, Long paymentId, LocalDateTime payTime);
    void handlePaymentSuccess(String orderNo, String paymentNo, BigDecimal amount, LocalDateTime payTime, Integer paymentMethod);
    void handlePaymentFailed(String orderNo, String paymentNo, String failReason, LocalDateTime failTime);
    void handlePaymentTimeout(String orderNo, String paymentNo, Integer timeoutMinutes, LocalDateTime timeoutTime);
}
