package com.pdd.product.repository;

import com.pdd.product.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    List<ProductSku> findByProductId(Long productId);
}
