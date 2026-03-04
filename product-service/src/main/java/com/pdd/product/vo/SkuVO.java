package com.pdd.product.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuVO {
    private Long id;
    private Long productId;
    private String attributes;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
}
