package com.pdd.product.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Integer sales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductSkuVO> skus;
}
