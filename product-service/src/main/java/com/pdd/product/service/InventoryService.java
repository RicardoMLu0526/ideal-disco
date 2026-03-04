package com.pdd.product.service;

import com.pdd.product.entity.Product;
import com.pdd.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        // 1. 获取分布式锁
        String lockKey = "lock:product:stock:" + productId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 3, TimeUnit.SECONDS);
        if (!locked) {
            throw new RuntimeException("系统繁忙，请稍后重试");
        }

        try {
            // 2. 查询商品库存
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 3. 检查库存是否充足
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品库存不足");
            }

            // 4. 扣减库存
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // 5. 更新Redis缓存
            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
            return true;
        } finally {
            // 6. 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public void rollbackStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
            // 更新Redis缓存
            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        }
    }

    public Integer getStock(Long productId) {
        // 先从Redis获取
        Integer stock = (Integer) redisTemplate.opsForValue().get("product:stock:" + productId);
        if (stock != null) {
            return stock;
        }

        // 从数据库获取
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return 0;
        }

        // 缓存到Redis
        redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        return product.getStock();
    }

    @Transactional
    public boolean preDeductStock(Long productId, Integer quantity, String orderNo) {
        // 实现预扣库存逻辑
        return deductStock(productId, quantity);
    }

    @Transactional
    public void releaseStock(Long productId, Integer quantity, String orderNo) {
        // 实现释放库存逻辑
        rollbackStock(productId, quantity);
    }
}
