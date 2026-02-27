package com.pdd.user.service;

import com.pdd.user.dto.AddressDTO;
import com.pdd.user.vo.AddressVO;
import com.pdd.user.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 根据ID获取地址
     * @param id 地址ID
     * @return 地址DTO
     */
    AddressDTO getAddressById(Long id);
    
    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址DTO列表
     */
    List<AddressDTO> getAddressesByUserId(Long userId);
    
    /**
     * 获取用户默认地址
     * @param userId 用户ID
     * @return 默认地址DTO
     */
    AddressDTO getDefaultAddressByUserId(Long userId);
    
    /**
     * 创建地址
     * @param addressDTO 地址DTO
     * @return 创建的地址DTO
     */
    AddressDTO createAddress(AddressDTO addressDTO);
    
    /**
     * 更新地址
     * @param id 地址ID
     * @param addressDTO 地址DTO
     * @return 更新后的地址DTO
     */
    AddressDTO updateAddress(Long id, AddressDTO addressDTO);
    
    /**
     * 删除地址
     * @param id 地址ID
     */
    void deleteAddress(Long id);
    
    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void setDefaultAddress(Long userId, Long addressId);
    
    /**
     * 转换地址实体为DTO
     * @param address 地址实体
     * @return 地址DTO
     */
    AddressDTO convertToDTO(Address address);
    
    /**
     * 转换地址DTO为VO
     * @param addressDTO 地址DTO
     * @return 地址VO
     */
    AddressVO convertToVO(AddressDTO addressDTO);
}