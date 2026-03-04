package com.pdd.product.service;

import com.pdd.product.entity.Product;
import com.pdd.product.entity.Sku;
import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.vo.ProductVO;
import com.pdd.product.vo.SkuVO;
import com.pdd.product.repository.ProductRepository;
import com.pdd.product.repository.SkuRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SkuRepository skuRepository;

    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product = productRepository.save(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    public List<ProductDTO> getProductList(ProductQueryDTO queryDTO) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return null;
        }
        Product product = productOptional.get();
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    public ProductVO getProductDetail(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return null;
        }
        Product product = productOptional.get();
        
        List<Sku> skus = skuRepository.findByProductId(productId);
        List<SkuVO> skuVOs = skus.stream()
                .map(sku -> {
                    SkuVO vo = new SkuVO();
                    BeanUtils.copyProperties(sku, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        vo.setSkus(skuVOs);
        return vo;
    }

    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return null;
        }
        Product product = productOptional.get();
        BeanUtils.copyProperties(productDTO, product);
        product = productRepository.save(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public void updateProductStatus(Long productId, Integer status) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setStatus(status);
            productRepository.save(product);
        }
    }

    public void updateProductStock(Long productId, Integer stock) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setStock(stock);
            productRepository.save(product);
        }
    }
}
