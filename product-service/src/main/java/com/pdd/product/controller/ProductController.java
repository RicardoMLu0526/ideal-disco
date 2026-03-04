package com.pdd.product.controller;

import com.pdd.product.dto.ProductDTO;
import com.pdd.product.dto.ProductQueryDTO;
import com.pdd.product.vo.ProductVO;
import com.pdd.product.vo.Result;
import com.pdd.product.service.ProductService;
import com.pdd.product.service.InventoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public Result<ProductVO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(created, vo);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<ProductVO>> getProductList(ProductQueryDTO queryDTO) {
        List<ProductDTO> dtos = productService.getProductList(queryDTO);
        List<ProductVO> vos = dtos.stream()
                .map(dto -> {
                    ProductVO vo = new ProductVO();
                    BeanUtils.copyProperties(dto, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        if (vo == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        if (updated == null) {
            return Result.error(404, "商品不存在");
        }
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(updated, vo);
        return Result.success(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        productService.updateProductStatus(id, request.getStatus());
        return Result.success();
    }

    @GetMapping("/{id}/stock")
    public Result<Integer> getProductStock(@PathVariable Long id) {
        Integer stock = inventoryService.getStock(id);
        return Result.success(stock);
    }

    // 库存扣减请求
    @PostMapping("/stock/deduct")
    public Result<Void> deductStock(@RequestBody StockDeductRequest request) {
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return Result.success();
    }

    // 预扣库存请求
    @PostMapping("/stock/pre-deduct")
    public Result<Void> preDeductStock(@RequestBody StockDeductRequest request) {
        inventoryService.preDeductStock(request.getProductId(), request.getQuantity(), request.getOrderNo());
        return Result.success();
    }

    // 释放库存请求
    @PostMapping("/stock/release")
    public Result<Void> releaseStock(@RequestBody StockReleaseRequest request) {
        inventoryService.releaseStock(request.getProductId(), request.getQuantity(), request.getOrderNo());
        return Result.success();
    }

    // 内部类：状态更新请求
    static class StatusRequest {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    // 内部类：库存扣减请求
    static class StockDeductRequest {
        private Long productId;
        private Integer quantity;
        private String orderNo;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
    }

    // 内部类：库存释放请求
    static class StockReleaseRequest {
        private Long productId;
        private Integer quantity;
        private String orderNo;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
    }
}
