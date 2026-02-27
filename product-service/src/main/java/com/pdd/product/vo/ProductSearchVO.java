package com.pdd.product.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String mainImage;
    private Integer sales;
}
