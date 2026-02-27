package com.pdd.user.service;

import com.pdd.user.dto.UserDTO;
import com.pdd.user.vo.UserVO;
import com.pdd.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户DTO
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户DTO
     */
    UserDTO getUserByPhone(String phone);
    
    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户DTO
     */
    UserDTO getUserByEmail(String email);
    
    /**
     * 获取所有用户
     * @return 用户DTO列表
     */
    List<UserDTO> getAllUsers();
    
    /**
     * 创建用户
     * @param userDTO 用户DTO
     * @return 创建的用户DTO
     */
    UserDTO createUser(UserDTO userDTO);
    
    /**
     * 更新用户
     * @param id 用户ID
     * @param userDTO 用户DTO
     * @return 更新后的用户DTO
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 转换用户实体为DTO
     * @param user 用户实体
     * @return 用户DTO
     */
    UserDTO convertToDTO(User user);
    
    /**
     * 转换用户DTO为VO
     * @param userDTO 用户DTO
     * @return 用户VO
     */
    UserVO convertToVO(UserDTO userDTO);
}