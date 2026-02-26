package com.pdd.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String url;
    private Integer sort;
    private Integer isMain;
    private LocalDateTime createdAt;
}
