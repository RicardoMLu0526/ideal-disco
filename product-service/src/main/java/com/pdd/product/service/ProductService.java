package com.pdd.product.service;

import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.vo.ProductVO;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    List<ProductDTO> getProductList(ProductQueryDTO queryDTO);
    ProductVO getProductById(Long productId);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    void deleteProduct(Long productId);
    void updateProductStatus(Long productId, Integer status);
    Integer getProductStock(Long productId);
}
