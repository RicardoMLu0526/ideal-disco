package com.pdd.product.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private String keyword;
    private Long categoryId;
    private Integer page;
    private Integer size;
}
