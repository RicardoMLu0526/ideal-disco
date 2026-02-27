package com.pdd.user.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    @Test
    void testSettersAndGetters() {
        // 测试setter和getter方法
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        assertEquals("testuser", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void testEmptyConstructor() {
        // 测试空构造函数
        LoginDTO dto = new LoginDTO();
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }
}