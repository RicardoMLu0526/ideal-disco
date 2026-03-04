package com.pdd.product.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status;
}
