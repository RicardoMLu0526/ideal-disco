package com.pdd.order.vo;

import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private Integer paymentStatus;
    private Integer orderStatus;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime confirmTime;
    private LocalDateTime cancelTime;
    private String trackingNumber;
    private List<OrderItem> items;

    // 从实体类转换
    public static OrderVO fromEntity(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setActualAmount(order.getActualAmount());
        vo.setPaymentStatus(order.getPaymentStatus());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setShippingAddress(order.getShippingAddress());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setPaymentTime(order.getPaymentTime());
        vo.setShippingTime(order.getShippingTime());
        vo.setConfirmTime(order.getConfirmTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setTrackingNumber(order.getTrackingNumber());
        vo.setItems(items);
        return vo;
    }
}
