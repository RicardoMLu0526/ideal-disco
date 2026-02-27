package com.pdd.user.vo;

import com.pdd.user.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserVOTest {

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

        // 转换为VO
        UserVO vo = UserVO.fromEntity(user);

        // 验证转换结果
        assertNotNull(vo);
        assertEquals(1L, vo.getUserId());
        assertEquals("testuser", vo.getUsername());
        assertEquals("13800138000", vo.getPhone());
        assertEquals("test@example.com", vo.getEmail());
        assertEquals("Test User", vo.getNickname());
        assertEquals("https://example.com/avatar.jpg", vo.getAvatar());
        assertEquals(1, vo.getStatus());
    }

    @Test
    void testFromEntityWithNull() {
        // 测试空实体转换
        UserVO vo = UserVO.fromEntity(null);
        assertNull(vo);
    }

    @Test
    void testSettersAndGetters() {
        // 测试setter和getter方法
        UserVO vo = new UserVO();
        vo.setUserId(2L);
        vo.setUsername("testuser2");
        vo.setPhone("13900139000");
        vo.setEmail("test2@example.com");
        vo.setNickname("Test User 2");
        vo.setAvatar("https://example.com/avatar2.jpg");
        vo.setStatus(0);

        assertEquals(2L, vo.getUserId());
        assertEquals("testuser2", vo.getUsername());
        assertEquals("13900139000", vo.getPhone());
        assertEquals("test2@example.com", vo.getEmail());
        assertEquals("Test User 2", vo.getNickname());
        assertEquals("https://example.com/avatar2.jpg", vo.getAvatar());
        assertEquals(0, vo.getStatus());
    }
}