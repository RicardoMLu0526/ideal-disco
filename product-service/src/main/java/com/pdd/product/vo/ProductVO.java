package com.pdd.product.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String brand;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private List<SkuVO> skus;
    private List<String> images;
}
