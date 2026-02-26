package com.pdd.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StockServiceFeign stockServiceFeign;

    /**
     * 预扣库存
     */
    public boolean preDeductStock(Long productId, Integer quantity) {
        // 1. 生成库存键
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 获取当前库存
        Integer currentStock = (Integer) redisTemplate.opsForValue().get(stockKey);
        if (currentStock == null) {
            // 从商品服务获取库存
            currentStock = getStockFromProductService(productId);
            redisTemplate.opsForValue().set(stockKey, currentStock);
        }

        // 3. 获取预扣库存
        Integer preDeductStock = (Integer) redisTemplate.opsForValue().get(preDeductKey);
        if (preDeductStock == null) {
            preDeductStock = 0;
        }

        // 4. 检查库存是否充足
        if (currentStock - preDeductStock < quantity) {
            return false;
        }

        // 5. 预扣库存
        redisTemplate.opsForValue().increment(preDeductKey, quantity);
        return true;
    }

    /**
     * 确认扣减库存
     */
    public void confirmDeductStock(Long productId, Integer quantity) {
        // 1. 生成库存键
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 扣减库存
        redisTemplate.opsForValue().decrement(stockKey, quantity);
        // 3. 减少预扣库存
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);

        // 4. 调用商品服务扣减数据库库存
        deductStockFromProductService(productId, quantity);
    }

    /**
     * 释放库存
     */
    public void releaseStock(Long productId, Integer quantity) {
        // 生成预扣库存键
        String preDeductKey = "product:pre_deduct:" + productId;
        // 减少预扣库存
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);
    }

    /**
     * 从商品服务获取库存
     */
    private Integer getStockFromProductService(Long productId) {
        // 调用商品服务获取库存
        return stockServiceFeign.getProductStock(productId);
    }

    /**
     * 调用商品服务扣减库存
     */
    private void deductStockFromProductService(Long productId, Integer quantity) {
        // 调用库存服务扣减库存
        Map<String, Object> stockRequest = Map.of(
                "productId", productId,
                "quantity", quantity
        );
        stockServiceFeign.decreaseStock(stockRequest);
    }
}
