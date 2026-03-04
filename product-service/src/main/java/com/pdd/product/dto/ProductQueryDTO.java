package com.pdd.product.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private Long categoryId;
    private String keyword;
    private Integer page;
    private Integer size;
}
