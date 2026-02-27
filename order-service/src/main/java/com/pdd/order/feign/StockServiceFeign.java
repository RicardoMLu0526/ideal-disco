package com.pdd.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "product-service", path = "/product")
public interface StockServiceFeign {

    @GetMapping("/api/stock")
    Integer getProductStock(@RequestParam("productId") Long productId);

    @PostMapping("/api/stock/decrease")
    Map<String, Object> decreaseStock(@RequestBody Map<String, Object> request);

    @PostMapping("/api/stock/increase")
    Map<String, Object> increaseStock(@RequestBody Map<String, Object> request);
}
