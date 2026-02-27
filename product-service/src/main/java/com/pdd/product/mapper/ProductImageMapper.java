package com.pdd.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.product.entity.ProductImage;
import java.util.List;

public interface ProductImageMapper extends BaseMapper<ProductImage> {
    List<ProductImage> selectByProductId(Long productId);
}
