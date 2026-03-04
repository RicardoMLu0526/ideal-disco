package com.pdd.product.service;

import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.entity.Product;
import com.pdd.product.repository.ProductRepository;
import com.pdd.product.repository.ProductSkuRepository;
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
    private ProductRepository productRepository;

    @Mock
    private ProductSkuRepository productSkuRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testCreateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("测试商品");
        productDTO.setPrice(java.math.BigDecimal.valueOf(99.99));
        productDTO.setStock(100);

        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(java.math.BigDecimal.valueOf(99.99));
        product.setStock(100);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(productDTO);
        assertNotNull(result);
        assertEquals("测试商品", result.getName());
    }
}
