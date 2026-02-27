package com.pdd.product.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSkuDTO {
    private Long id;
    private Long productId;
    private String skuCode;
    private JsonNode attributes;
    private BigDecimal price;
    private Integer stock;
}
