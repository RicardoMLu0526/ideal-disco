package com.pdd.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long productSkuId;
    private String productName;
    private String productSku;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
