package com.pdd.product.vo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSkuVO {
    private Long id;
    private Long productId;
    private String skuCode;
    private JsonNode attributes;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
