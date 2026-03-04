package com.pdd.product.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productId;
    private String url;
    private Integer sort;
    
    @Column(name = "is_main")
    private Integer isMain;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
