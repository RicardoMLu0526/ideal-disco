package com.pdd.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "payment-service", path = "/payment")
public interface PaymentServiceFeign {

    @PostMapping("/api/payments")
    Map<String, Object> createPayment(@RequestBody Map<String, Object> request);

    @PostMapping("/api/payments/{id}/callback")
    Map<String, Object> paymentCallback(@PathVariable("id") Long id, @RequestBody Map<String, Object> request);
}
