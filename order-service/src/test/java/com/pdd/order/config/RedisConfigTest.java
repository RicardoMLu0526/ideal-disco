package com.pdd.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisTemplateBean() {
        assertNotNull(redisTemplate, "RedisTemplate bean should be created");
        assertNotNull(redisTemplate.getKeySerializer(), "Key serializer should be set");
        assertNotNull(redisTemplate.getValueSerializer(), "Value serializer should be set");
    }
}
