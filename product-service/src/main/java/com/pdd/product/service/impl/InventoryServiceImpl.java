package com.pdd.product.service.impl;

import com.pdd.product.entity.Product;
import com.pdd.product.mapper.ProductMapper;
import com.pdd.product.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        String lockKey = "lock:product:stock:" + productId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 3, TimeUnit.SECONDS);
        if (!locked) {
            throw new RuntimeException("系统繁忙，请稍后重试");
        }

        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            if (product.getStock() < quantity) {
                throw new RuntimeException("商品库存不足");
            }

            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);

            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
            return true;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional
    public void rollbackStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            productMapper.updateById(product);
            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        }
    }

    @Override
    public Integer getStock(Long productId) {
        Integer stock = (Integer) redisTemplate.opsForValue().get("product:stock:" + productId);
        if (stock != null) {
            return stock;
        }

        Product product = productMapper.selectById(productId);
        if (product == null) {
            return 0;
        }

        redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        return product.getStock();
    }
}
