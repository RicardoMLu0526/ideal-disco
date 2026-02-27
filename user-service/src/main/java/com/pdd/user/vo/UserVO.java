package com.pdd.user.vo;

import com.pdd.user.entity.User;

/**
 * 用户视图对象
 * 用于前端展示，不包含敏感信息
 */
public class UserVO {
    private Long userId;         // 用户ID
    private String username;     // 用户名
    private String phone;        // 手机号
    private String email;        // 邮箱
    private String nickname;     // 昵称
    private String avatar;       // 头像URL
    private Integer status;      // 状态

    // 从实体类转换为VO
    public static UserVO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        return vo;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}