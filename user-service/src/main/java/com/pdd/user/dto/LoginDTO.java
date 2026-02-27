package com.pdd.user.dto;

/**
 * 登录数据传输对象
 */
public class LoginDTO {
    private String username;     // 用户名/手机号/邮箱，用于登录
    private String password;     // 密码，用于登录

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}