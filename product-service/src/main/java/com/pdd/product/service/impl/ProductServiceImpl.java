package com.pdd.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.entity.Product;
import com.pdd.product.entity.ProductSku;
import com.pdd.product.mapper.ProductMapper;
import com.pdd.product.mapper.ProductSkuMapper;
import com.pdd.product.service.ProductService;
import com.pdd.product.vo.ProductSkuVO;
import com.pdd.product.vo.ProductVO;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private RestHighLevelClient esClient;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        productMapper.insert(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Override
    public List<ProductDTO> getProductList(ProductQueryDTO queryDTO) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (queryDTO.getKeyword() != null) {
            wrapper.like("name", queryDTO.getKeyword());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq("category_id", queryDTO.getCategoryId());
        }
        List<Product> products = productMapper.selectList(wrapper);
        return products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(product, productDTO);
            return productDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        List<ProductSku> skus = productSkuMapper.selectByProductId(productId);
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setSkus(skus.stream().map(sku -> {
            ProductSkuVO skuVO = new ProductSkuVO();
            BeanUtils.copyProperties(sku, skuVO);
            return skuVO;
        }).collect(Collectors.toList()));
        return productVO;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        BeanUtils.copyProperties(productDTO, product);
        productMapper.updateById(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        productMapper.deleteById(productId);
    }

    @Override
    public void updateProductStatus(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        // 这里可以添加同步到ES的逻辑
    }

    @Override
    public Integer getProductStock(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return product.getStock();
    }
}
