package com.pdd.product.controller;

import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.service.ProductService;
import com.pdd.product.vo.ProductVO;
import com.pdd.product.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Result<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO result = productService.createProduct(productDTO);
        return Result.success(result);
    }

    @GetMapping
    public Result<List<ProductDTO>> getProductList(ProductQueryDTO queryDTO) {
        List<ProductDTO> products = productService.getProductList(queryDTO);
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id) {
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    public Result<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO result = productService.updateProduct(id, productDTO);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody StatusRequest statusRequest) {
        productService.updateProductStatus(id, statusRequest.getStatus());
        return Result.success();
    }

    @GetMapping("/{id}/stock")
    public Result<StockResponse> getProductStock(@PathVariable Long id) {
        Integer stock = productService.getProductStock(id);
        StockResponse response = new StockResponse();
        response.setStock(stock);
        return Result.success(response);
    }

    @PostMapping("/{id}/images")
    public Result<String> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // 这里可以实现图片上传逻辑
        return Result.success("图片上传成功");
    }

    static class StatusRequest {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    static class StockResponse {
        private Integer stock;

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }
}
