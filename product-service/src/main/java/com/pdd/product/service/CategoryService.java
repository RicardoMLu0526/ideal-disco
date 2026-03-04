package com.pdd.product.service;

import com.pdd.product.entity.Category;
import com.pdd.product.dto.CategoryDTO;
import com.pdd.product.vo.CategoryVO;
import com.pdd.product.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category = categoryRepository.save(category);
        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }

    public List<CategoryDTO> getCategoryList() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    BeanUtils.copyProperties(category, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            return null;
        }
        Category category = categoryOptional.get();
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {
            return null;
        }
        Category category = categoryOptional.get();
        BeanUtils.copyProperties(categoryDTO, category);
        category = categoryRepository.save(category);
        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
