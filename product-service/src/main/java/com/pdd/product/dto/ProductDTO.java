package com.pdd.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
