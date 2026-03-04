package com.pdd.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuDTO {
    private Long id;
    private Long productId;
    private String attributes;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
