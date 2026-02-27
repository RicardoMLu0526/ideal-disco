package com.pdd.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
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
}
