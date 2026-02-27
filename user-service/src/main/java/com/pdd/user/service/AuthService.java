package com.pdd.user.service;

import com.pdd.user.dto.LoginDTO;
import com.pdd.user.dto.UserDTO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     * @param loginDTO 登录DTO
     * @return 包含token和用户信息的Map
     */
    Map<String, Object> login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     * @param userDTO 用户DTO
     * @return 注册成功的用户DTO
     */
    UserDTO register(UserDTO userDTO);
    
    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 新的token
     */
    String refreshToken(String refreshToken);
    
    /**
     * 验证token
     * @param token token
     * @return 验证结果
     */
    boolean validateToken(String token);
    
    /**
     * 从token中获取用户信息
     * @param token token
     * @return 用户DTO
     */
    UserDTO getUserFromToken(String token);
}