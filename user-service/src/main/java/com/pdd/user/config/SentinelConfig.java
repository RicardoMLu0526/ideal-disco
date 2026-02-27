package com.pdd.user.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class SentinelConfig {

    @Bean
    public Function<BlockException, String> blockExceptionHandler() {
        return exception -> {
            String className = exception.getClass().getSimpleName();
            switch (className) {
                case "FlowException":
                    return "请求过于频繁，请稍后再试";
                case "DegradeException":
                    return "服务暂时不可用，请稍后再试";
                case "AuthorityException":
                    return "访问被拒绝";
                case "ParamFlowException":
                    return "参数限流，请稍后再试";
                default:
                    return "系统繁忙，请稍后再试";
            }
        };
    }
}
