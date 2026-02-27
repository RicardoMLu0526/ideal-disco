package com.pdd.product.service;

import com.pdd.product.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private com.pdd.product.mapper.ProductMapper productMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    public void testDeductStock() {
        com.pdd.product.entity.Product product = new com.pdd.product.entity.Product();
        product.setId(1L);
        product.setStock(100);

        when(productMapper.selectById(1L)).thenReturn(product);
        when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);

        boolean result = inventoryService.deductStock(1L, 10);
        assertTrue(result);
    }
}
