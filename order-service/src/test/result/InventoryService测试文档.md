```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\service\InventoryService.java
package com.pdd.order.service;

import com.pdd.order.feign.StockServiceFeign;
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
     * 棰勬墸搴撳瓨
     */
    public boolean preDeductStock(Long productId, Integer quantity) {
        // 1. 鐢熸垚搴撳瓨閿?
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 鑾峰彇褰撳墠搴撳瓨
        Integer currentStock = (Integer) redisTemplate.opsForValue().get(stockKey);
        if (currentStock == null) {
            // 浠庡晢鍝佹湇鍔¤幏鍙栧簱瀛?
            currentStock = getStockFromProductService(productId);
            redisTemplate.opsForValue().set(stockKey, currentStock);
        }

        // 3. 鑾峰彇棰勬墸搴撳瓨
        Integer preDeductStock = (Integer) redisTemplate.opsForValue().get(preDeductKey);
        if (preDeductStock == null) {
            preDeductStock = 0;
        }

        // 4. 妫€鏌ュ簱瀛樻槸鍚﹀厖瓒?
        if (currentStock - preDeductStock < quantity) {
            return false;
        }

        // 5. 棰勬墸搴撳瓨
        redisTemplate.opsForValue().increment(preDeductKey, quantity);
        return true;
    }

    /**
     * 纭鎵ｅ噺搴撳瓨
     */
    public void confirmDeductStock(Long productId, Integer quantity) {
        // 1. 鐢熸垚搴撳瓨閿?
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 鎵ｅ噺搴撳瓨
        redisTemplate.opsForValue().decrement(stockKey, quantity);
        // 3. 鍑忓皯棰勬墸搴撳瓨
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);

        // 4. 璋冪敤鍟嗗搧鏈嶅姟鎵ｅ噺鏁版嵁搴撳簱瀛?
        deductStockFromProductService(productId, quantity);
    }

    /**
     * 閲婃斁搴撳瓨
     */
    public void releaseStock(Long productId, Integer quantity) {
        // 鐢熸垚棰勬墸搴撳瓨閿?
        String preDeductKey = "product:pre_deduct:" + productId;
        // 鍑忓皯棰勬墸搴撳瓨
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);
    }

    /**
     * 浠庡晢鍝佹湇鍔¤幏鍙栧簱瀛?
     */
    private Integer getStockFromProductService(Long productId) {
        // 璋冪敤鍟嗗搧鏈嶅姟鑾峰彇搴撳瓨
        return stockServiceFeign.getProductStock(productId);
    }

    /**
     * 璋冪敤鍟嗗搧鏈嶅姟鎵ｅ噺搴撳瓨
     */
    private void deductStockFromProductService(Long productId, Integer quantity) {
        // 璋冪敤搴撳瓨鏈嶅姟鎵ｅ噺搴撳瓨
        Map<String, Object> stockRequest = Map.of(
                "productId", productId,
                "quantity", quantity
        );
        stockServiceFeign.decreaseStock(stockRequest);
    }
}

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.service.InventoryServiceTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```