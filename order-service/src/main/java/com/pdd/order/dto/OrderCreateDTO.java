package com.pdd.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    private Long userId;
    private List<OrderItemDTO> items = new java.util.ArrayList<>();
    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
}
