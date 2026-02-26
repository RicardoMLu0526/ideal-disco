package com.pdd.product.service;

import com.pdd.product.dto.CategoryDTO;
import com.pdd.product.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private com.pdd.product.mapper.CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void testCreateCategory() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("测试分类");
        categoryDTO.setParentId(0L);
        categoryDTO.setLevel(1);

        com.pdd.product.entity.Category category = new com.pdd.product.entity.Category();
        category.setId(1L);
        category.setName("测试分类");
        category.setParentId(0L);
        category.setLevel(1);

        when(categoryMapper.insert(any())).thenReturn(1);

        CategoryDTO result = categoryService.createCategory(categoryDTO);
        assertNotNull(result);
        assertEquals("测试分类", result.getName());
    }
}
