package com.pdd.product.repository;

import com.pdd.product.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    List<Sku> findByProductId(Long productId);
}
