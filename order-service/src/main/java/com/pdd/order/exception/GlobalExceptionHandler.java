package com.pdd.order.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException: {}", e.getMessage(), e);
        return Map.of("code", 500, "message", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return Map.of("code", 500, "message", "系统内部错误");
    }
}
