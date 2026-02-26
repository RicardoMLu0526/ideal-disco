package com.pdd.product.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
