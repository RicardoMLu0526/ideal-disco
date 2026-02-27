package com.pdd.product.dto;

import lombok.Data;

@Data
public class SearchDTO {
    private String keyword;
    private Integer page;
    private Integer size;
}
