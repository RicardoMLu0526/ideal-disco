package com.pdd.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_sku")
public class ProductSku {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String skuCode;
    private JsonNode attributes;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
