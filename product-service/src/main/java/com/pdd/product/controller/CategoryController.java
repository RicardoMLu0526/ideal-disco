package com.pdd.product.controller;

import com.pdd.product.dto.CategoryDTO;
import com.pdd.product.service.CategoryService;
import com.pdd.product.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO result = categoryService.createCategory(categoryDTO);
        return Result.success(result);
    }

    @GetMapping
    public Result<List<CategoryDTO>> getCategoryList() {
        List<CategoryDTO> categories = categoryService.getCategoryList();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO result = categoryService.updateCategory(id, categoryDTO);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
