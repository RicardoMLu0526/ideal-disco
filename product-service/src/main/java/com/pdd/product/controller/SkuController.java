package com.pdd.product.controller;

import com.pdd.product.dto.SkuDTO;
import com.pdd.product.vo.SkuVO;
import com.pdd.product.vo.Result;
import com.pdd.product.service.SkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products/{productId}/skus")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @PostMapping
    public Result<SkuVO> createSku(@PathVariable Long productId, @RequestBody SkuDTO skuDTO) {
        SkuDTO created = skuService.createSku(productId, skuDTO);
        SkuVO vo = new SkuVO();
        BeanUtils.copyProperties(created, vo);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<SkuVO>> getSkuList(@PathVariable Long productId) {
        List<SkuDTO> dtos = skuService.getSkuList(productId);
        List<SkuVO> vos = dtos.stream()
                .map(dto -> {
                    SkuVO vo = new SkuVO();
                    BeanUtils.copyProperties(dto, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/{id}")
    public Result<SkuVO> getSkuById(@PathVariable Long productId, @PathVariable Long id) {
        SkuDTO dto = skuService.getSkuById(id);
        if (dto == null) {
            return Result.error(404, "SKU不存在");
        }
        SkuVO vo = new SkuVO();
        BeanUtils.copyProperties(dto, vo);
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<SkuVO> updateSku(@PathVariable Long productId, @PathVariable Long id, @RequestBody SkuDTO skuDTO) {
        SkuDTO updated = skuService.updateSku(id, skuDTO);
        if (updated == null) {
            return Result.error(404, "SKU不存在");
        }
        SkuVO vo = new SkuVO();
        BeanUtils.copyProperties(updated, vo);
        return Result.success(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSku(@PathVariable Long productId, @PathVariable Long id) {
        skuService.deleteSku(id);
        return Result.success();
    }
}
