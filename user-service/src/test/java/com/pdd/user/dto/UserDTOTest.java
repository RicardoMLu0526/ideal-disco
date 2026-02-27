package com.pdd.user.dto;

import com.pdd.user.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testFromEntity() {
        // 创建User实体
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setNickname("Test User");
        user.setAvatar("https://example.com/avatar.jpg");
        user.setStatus(1);

        // 转换为DTO
        UserDTO dto = UserDTO.fromEntity(user);

        // 验证转换结果
        assertNotNull(dto);
        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("13800138000", dto.getPhone());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getNickname());
        assertEquals("https://example.com/avatar.jpg", dto.getAvatar());
        assertEquals(1, dto.getStatus());
    }

    @Test
    void testFromEntityWithNull() {
        // 测试空实体转换
        UserDTO dto = UserDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        // 测试setter和getter方法
        UserDTO dto = new UserDTO();
        dto.setUserId(2L);
        dto.setUsername("testuser2");
        dto.setPhone("13900139000");
        dto.setEmail("test2@example.com");
        dto.setNickname("Test User 2");
        dto.setAvatar("https://example.com/avatar2.jpg");
        dto.setStatus(0);

        assertEquals(2L, dto.getUserId());
        assertEquals("testuser2", dto.getUsername());
        assertEquals("13900139000", dto.getPhone());
        assertEquals("test2@example.com", dto.getEmail());
        assertEquals("Test User 2", dto.getNickname());
        assertEquals("https://example.com/avatar2.jpg", dto.getAvatar());
        assertEquals(0, dto.getStatus());
    }
}