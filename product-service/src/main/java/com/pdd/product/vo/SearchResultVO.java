package com.pdd.product.vo;

import lombok.Data;
import java.util.List;

@Data
public class SearchResultVO {
    private Long total;
    private List<ProductSearchVO> products;
}
