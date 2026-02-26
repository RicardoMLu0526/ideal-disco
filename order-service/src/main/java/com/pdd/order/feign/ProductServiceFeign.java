package com.pdd.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "product-service", path = "/product")
public interface ProductServiceFeign {

    @GetMapping("/api/products/{id}")
    Map<String, Object> getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/products/{id}/price")
    BigDecimal getProductPrice(@PathVariable("id") Long id);

    @GetMapping("/api/products/{id}/stock")
    Integer getProductStock(@PathVariable("id") Long id);
}
