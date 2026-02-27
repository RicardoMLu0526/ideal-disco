package com.pdd.user.vo;

import com.pdd.user.entity.Address;

/**
 * 地址视图对象
 * 用于前端展示
 */
public class AddressVO {
    private Long id;             // 地址ID
    private Long userId;         // 用户ID
    private String name;         // 收货人姓名
    private String phone;        // 收货人电话
    private String province;     // 省份
    private String city;         // 城市
    private String district;     // 区县
    private String detail;       // 详细地址
    private Integer isDefault;   // 是否默认地址 (0:否, 1:是)

    // 从实体类转换为VO
    public static AddressVO fromEntity(Address address) {
        if (address == null) {
            return null;
        }
        AddressVO vo = new AddressVO();
        vo.setId(address.getId());
        vo.setUserId(address.getUserId());
        vo.setName(address.getName());
        vo.setPhone(address.getPhone());
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setDetail(address.getDetail());
        vo.setIsDefault(address.getIsDefault());
        return vo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}