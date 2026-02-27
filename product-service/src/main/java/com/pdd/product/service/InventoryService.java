package com.pdd.product.service;

public interface InventoryService {
    boolean deductStock(Long productId, Integer quantity);
    void rollbackStock(Long productId, Integer quantity);
    Integer getStock(Long productId);
}
