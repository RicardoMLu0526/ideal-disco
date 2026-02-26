package com.pdd.product.service;

import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private com.pdd.product.mapper.ProductMapper productMapper;

    @Mock
    private com.pdd.product.mapper.ProductSkuMapper productSkuMapper;

    @Mock
    private org.elasticsearch.client.RestHighLevelClient esClient;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void testCreateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("测试商品");
        productDTO.setPrice(java.math.BigDecimal.valueOf(99.99));
        productDTO.setStock(100);

        com.pdd.product.entity.Product product = new com.pdd.product.entity.Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(java.math.BigDecimal.valueOf(99.99));
        product.setStock(100);

        when(productMapper.insert(any())).thenReturn(1);

        ProductDTO result = productService.createProduct(productDTO);
        assertNotNull(result);
        assertEquals("测试商品", result.getName());
    }
}
