package com.pdd.product.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sku")
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productId;
    private String attributes;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
