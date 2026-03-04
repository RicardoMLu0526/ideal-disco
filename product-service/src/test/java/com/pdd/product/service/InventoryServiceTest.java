package com.pdd.product.service;

import com.pdd.product.entity.Product;
import com.pdd.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    public void testDeductStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(redisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);

        boolean result = inventoryService.deductStock(1L, 10);
        assertTrue(result);
    }
}
