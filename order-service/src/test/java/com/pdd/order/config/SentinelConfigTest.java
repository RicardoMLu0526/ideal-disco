package com.pdd.order.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SentinelConfigTest {

    @Autowired
    private SentinelResourceAspect sentinelResourceAspect;

    @Test
    void testSentinelResourceAspectBean() {
        assertNotNull(sentinelResourceAspect, "SentinelResourceAspect bean should be created");
    }
}
