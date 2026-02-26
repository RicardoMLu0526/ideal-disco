package com.pdd.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密与验证工具类
 */
@Component
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;

    // 注入 Spring 提供的 BCrypt 加密器（推荐方式）
    public PasswordUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 简化构造器（非 Spring 环境使用）
     */
    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 密码加密
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 密码验证
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}