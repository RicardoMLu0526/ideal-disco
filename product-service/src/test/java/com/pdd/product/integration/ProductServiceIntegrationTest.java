package com.pdd.product.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceIntegrationTest {

    @Autowired
    private com.pdd.product.service.ProductService productService;

    @Autowired
    private com.pdd.product.service.CategoryService categoryService;

    @Test
    public void testProductServiceFlow() {
        // 测试完整的产品服务流程
        // 1. 创建分类
        com.pdd.product.dto.CategoryDTO categoryDTO = new com.pdd.product.dto.CategoryDTO();
        categoryDTO.setName("测试分类");
        categoryDTO.setParentId(0L);
        categoryDTO.setLevel(1);
        com.pdd.product.dto.CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        assertNotNull(createdCategory);

        // 2. 创建产品
        com.pdd.product.dto.ProductDTO productDTO = new com.pdd.product.dto.ProductDTO();
        productDTO.setName("测试商品");
        productDTO.setCategoryId(createdCategory.getId());
        productDTO.setPrice(java.math.BigDecimal.valueOf(99.99));
        productDTO.setStock(100);
        com.pdd.product.dto.ProductDTO createdProduct = productService.createProduct(productDTO);
        assertNotNull(createdProduct);

        // 3. 获取产品详情
        com.pdd.product.vo.ProductVO productVO = productService.getProductById(createdProduct.getId());
        assertNotNull(productVO);
        assertEquals("测试商品", productVO.getName());
    }
}
