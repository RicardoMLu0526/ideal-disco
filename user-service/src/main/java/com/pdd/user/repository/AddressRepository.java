package com.pdd.user.repository;

import com.pdd.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 地址仓库接口
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * 根据用户ID查询地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * 根据用户ID和是否默认查询地址
     * @param userId 用户ID
     * @param isDefault 是否默认
     * @return 地址信息
     */
    Optional<Address> findByUserIdAndIsDefault(Long userId, Integer isDefault);
    
    /**
     * 检查用户是否有指定地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}