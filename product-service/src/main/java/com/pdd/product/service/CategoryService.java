package com.pdd.product.service;

import com.pdd.product.dto.CategoryDTO;
import com.pdd.product.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getCategoryList();
    CategoryDTO getCategoryById(Long categoryId);
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(Long categoryId);
}
