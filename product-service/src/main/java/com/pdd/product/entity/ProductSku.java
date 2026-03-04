package com.pdd.product.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_sku")
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String skuCode;
    
    @Column(columnDefinition = "json")
    private String attributesJson;
    
    private BigDecimal price;
    private Integer stock;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 非持久化字段，用于业务逻辑
    @Transient
    private JsonNode attributes;
    
    // 转换方法
    public void setAttributes(JsonNode attributes) {
        this.attributes = attributes;
        try {
            if (attributes != null) {
                this.attributesJson = new ObjectMapper().writeValueAsString(attributes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public JsonNode getAttributes() {
        try {
            if (attributesJson != null) {
                this.attributes = new ObjectMapper().readTree(attributesJson);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attributes;
    }
}
