package com.pdd.product.controller;

import com.pdd.product.dto.CategoryDTO;
import com.pdd.product.vo.CategoryVO;
import com.pdd.product.vo.Result;
import com.pdd.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<CategoryVO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(created, vo);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<CategoryVO>> getCategoryList() {
        List<CategoryDTO> dtos = categoryService.getCategoryList();
        List<CategoryVO> vos = dtos.stream()
                .map(dto -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(dto, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryDTO dto = categoryService.getCategoryById(id);
        if (dto == null) {
            return Result.error(404, "分类不存在");
        }
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(dto, vo);
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<CategoryVO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        if (updated == null) {
            return Result.error(404, "分类不存在");
        }
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(updated, vo);
        return Result.success(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
