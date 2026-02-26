package com.pdd.order.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IdGeneratorTest {

    @Autowired
    private IdGenerator idGenerator;

    @Test
    void testNextId() {
        Long id = idGenerator.nextId();
        assertNotNull(id, "Generated ID should not be null");
        assertTrue(id > 0, "Generated ID should be positive");
        
        // 测试ID唯一性
        Long id2 = idGenerator.nextId();
        assertTrue(!id.equals(id2), "Generated IDs should be unique");
    }
}
