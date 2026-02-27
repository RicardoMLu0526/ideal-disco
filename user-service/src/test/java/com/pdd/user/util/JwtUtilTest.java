package com.pdd.user.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateAndValidateToken() {
        // 1. 构建测试用户
        UserDetails userDetails = User.withUsername("testUser")
                .password("123456")
                .roles("USER")
                .build();

        // 2. 生成令牌
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        System.out.println("生成的JWT令牌：" + token);

        // 3. 提取用户名
        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);

        // 4. 验证令牌
        boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }
}