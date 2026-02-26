package com.pdd.user.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    private PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void encodeAndMatch() {
        // 1. 明文密码
        String rawPassword = "123456";

        // 2. 加密
        String encodedPassword = passwordUtil.encode(rawPassword);
        assertNotNull(encodedPassword);
        // 验证加密后的密码长度符合 BCrypt 规范（60位）
        assertEquals(60, encodedPassword.length());

        // 3. 验证正确密码
        boolean match1 = passwordUtil.matches(rawPassword, encodedPassword);
        assertTrue(match1);

        // 4. 验证错误密码
        boolean match2 = passwordUtil.matches("wrong", encodedPassword);
        assertFalse(match2);
    }
}