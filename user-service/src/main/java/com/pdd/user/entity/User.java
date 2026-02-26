package com.pdd.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 适配 Spring Boot + Spring Data JPA 框架
 */
@Data  // Lombok 注解，自动生成getter/setter/toString/equals/hashCode等方法
@Entity  // JPA注解，标识这是一个实体类
@Table(name = "tb_user")  // 指定数据库表名
@DynamicInsert  // 插入时只插入非空字段
@DynamicUpdate  // 更新时只更新修改过的字段
public class User {

    /**
     * 主键ID
     */
    @Id  // 标识主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键（适配MySQL）
    private Long id;

    /**
     * 用户名（唯一）
     */
    @NotBlank(message = "用户名不能为空")  // 参数校验：非空
    @Column(name = "username", unique = true, nullable = false, length = 50)  // 数据库字段映射
    private String username;

    /**
     * 密码（加密存储）
     */
    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(name = "phone", unique = true, length = 11)
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Column(name = "email", unique = true, length = 100)
    private String email;

    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 255)
    private String avatar;

    /**
     * 性别 (0:未知,1:男,2:女)
     */
    @Column(name = "gender")
    private Integer gender = 0;  // 默认值

    /**
     * 状态 (1:正常,0:禁用)
     */
    @Column(name = "status")
    private Integer status = 1;  // 默认值

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)  // 插入后不允许更新
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 插入前自动填充创建/更新时间
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新前自动填充更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}