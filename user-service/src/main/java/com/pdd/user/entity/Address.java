package com.pdd.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 地址实体类
 */
@Data
@Entity
@Table(name = "address")
@DynamicInsert
@DynamicUpdate
public class Address {

    /**
     * 地址ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    @Column(name = "province", nullable = false, length = 50)
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    @Column(name = "district", nullable = false, length = 50)
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Column(name = "detail", nullable = false, length = 255)
    private String detail;

    /**
     * 是否默认地址 (0:否, 1:是)
     */
    @Column(name = "is_default")
    private Integer isDefault = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
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