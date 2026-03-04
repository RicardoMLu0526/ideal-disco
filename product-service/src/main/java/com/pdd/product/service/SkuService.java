package com.pdd.product.service;

import com.pdd.product.entity.Sku;
import com.pdd.product.dto.SkuDTO;
import com.pdd.product.vo.SkuVO;
import com.pdd.product.repository.SkuRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkuService {

    @Autowired
    private SkuRepository skuRepository;

    public SkuDTO createSku(Long productId, SkuDTO skuDTO) {
        Sku sku = new Sku();
        BeanUtils.copyProperties(skuDTO, sku);
        sku.setProductId(productId);
        skuRepository.save(sku);
        BeanUtils.copyProperties(sku, skuDTO);
        return skuDTO;
    }

    public List<SkuDTO> getSkuList(Long productId) {
        List<Sku> skus = skuRepository.findByProductId(productId);
        return skus.stream()
                .map(sku -> {
                    SkuDTO dto = new SkuDTO();
                    BeanUtils.copyProperties(sku, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public SkuDTO getSkuById(Long skuId) {
        Sku sku = skuRepository.findById(skuId).orElse(null);
        if (sku == null) {
            return null;
        }
        SkuDTO dto = new SkuDTO();
        BeanUtils.copyProperties(sku, dto);
        return dto;
    }

    public SkuDTO updateSku(Long skuId, SkuDTO skuDTO) {
        Sku sku = skuRepository.findById(skuId).orElse(null);
        if (sku == null) {
            return null;
        }
        BeanUtils.copyProperties(skuDTO, sku);
        skuRepository.save(sku);
        BeanUtils.copyProperties(sku, skuDTO);
        return skuDTO;
    }

    public void deleteSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    public void updateSkuStock(Long skuId, Integer stock) {
        Sku sku = skuRepository.findById(skuId).orElse(null);
        if (sku != null) {
            sku.setStock(stock);
            skuRepository.save(sku);
        }
    }
}
