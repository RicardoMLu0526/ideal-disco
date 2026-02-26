package com.pdd.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
        // 测试应用上下文是否能正常加载
    }

    @Test
    void testApplicationStartup() {
        // 测试应用启动
        OrderServiceApplication.main(new String[]{});
    }
}
