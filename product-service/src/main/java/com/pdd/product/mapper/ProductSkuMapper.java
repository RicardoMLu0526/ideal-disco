package com.pdd.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.product.entity.ProductSku;
import java.util.List;

public interface ProductSkuMapper extends BaseMapper<ProductSku> {
    List<ProductSku> selectByProductId(Long productId);
}
