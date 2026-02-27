package com.pdd.order.service;

import com.pdd.order.feign.StockServiceFeign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

public class InventoryServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private StockServiceFeign stockServiceFeign;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testPreDeductStock_Success() {
        Long productId = 1L;
        Integer quantity = 2;
        Integer currentStock = 10;
        Integer preDeductStock = 0;

        when(valueOperations.get("product:stock:" + productId)).thenReturn(currentStock);
        when(valueOperations.get("product:pre_deduct:" + productId)).thenReturn(preDeductStock);
        when(valueOperations.increment("product:pre_deduct:" + productId, quantity)).thenReturn(2L);

        boolean result = inventoryService.preDeductStock(productId, quantity);

        assertTrue(result);
        verify(valueOperations, times(1)).get("product:stock:" + productId);
        verify(valueOperations, times(1)).get("product:pre_deduct:" + productId);
        verify(valueOperations, times(1)).increment("product:pre_deduct:" + productId, quantity);
    }

    @Test
    void testPreDeductStock_InsufficientStock() {
        Long productId = 1L;
        Integer quantity = 5;
        Integer currentStock = 3;
        Integer preDeductStock = 0;

        when(valueOperations.get("product:stock:" + productId)).thenReturn(currentStock);
        when(valueOperations.get("product:pre_deduct:" + productId)).thenReturn(preDeductStock);

        boolean result = inventoryService.preDeductStock(productId, quantity);

        assertFalse(result);
        verify(valueOperations, times(1)).get("product:stock:" + productId);
        verify(valueOperations, times(1)).get("product:pre_deduct:" + productId);
        verify(valueOperations, never()).increment(anyString(), anyInt());
    }

    @Test
    void testPreDeductStock_FetchStockFromService() {
        Long productId = 1L;
        Integer quantity = 2;
        Integer stockFromService = 10;

        when(valueOperations.get("product:stock:" + productId)).thenReturn(null);
        when(valueOperations.get("product:pre_deduct:" + productId)).thenReturn(null);
        when(stockServiceFeign.getProductStock(productId)).thenReturn(stockFromService);
        doNothing().when(valueOperations).set("product:stock:" + productId, stockFromService);
        when(valueOperations.increment("product:pre_deduct:" + productId, quantity)).thenReturn(2L);

        boolean result = inventoryService.preDeductStock(productId, quantity);

        assertTrue(result);
        verify(stockServiceFeign, times(1)).getProductStock(productId);
        verify(valueOperations, times(1)).set("product:stock:" + productId, stockFromService);
    }

    @Test
    void testConfirmDeductStock() {
        Long productId = 1L;
        Integer quantity = 2;

        when(valueOperations.decrement("product:stock:" + productId, quantity)).thenReturn(8L);
        when(valueOperations.decrement("product:pre_deduct:" + productId, quantity)).thenReturn(0L);

        inventoryService.confirmDeductStock(productId, quantity);

        verify(valueOperations, times(1)).decrement("product:stock:" + productId, quantity);
        verify(valueOperations, times(1)).decrement("product:pre_deduct:" + productId, quantity);
        verify(stockServiceFeign, times(1)).decreaseStock(Map.of("productId", productId, "quantity", quantity));
    }

    @Test
    void testReleaseStock() {
        Long productId = 1L;
        Integer quantity = 2;

        when(valueOperations.decrement("product:pre_deduct:" + productId, quantity)).thenReturn(0L);

        inventoryService.releaseStock(productId, quantity);

        verify(valueOperations, times(1)).decrement("product:pre_deduct:" + productId, quantity);
    }
}
