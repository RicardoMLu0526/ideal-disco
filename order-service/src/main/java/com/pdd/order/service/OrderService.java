package com.pdd.order.service;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderVO getOrderDetail(Long orderId);
    List<Order> getOrderList();
    Order createOrder(OrderCreateDTO createDTO);
    Order updateOrder(Long orderId, Order order);
    void deleteOrder(Long orderId);
    void cancelOrder(Long orderId);
    void payOrder(Long orderId, String paymentMethod);
    void confirmOrder(Long orderId);
    void shipOrder(Long orderId, String trackingNumber);
}
