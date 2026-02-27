package com.pdd.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密与验证工具类
 */
@Component
public class PasswordUtil {

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordUtil() {
    }

    @Autowired(required = false)
    public void setPasswordEncoder(PasswordEncoder encoder) {
        passwordEncoder = encoder;
    }

    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}