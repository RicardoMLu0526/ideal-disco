package com.pdd.user.service.impl;

import com.pdd.user.dto.LoginDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.entity.User;
import com.pdd.user.repository.UserRepository;
import com.pdd.user.service.AuthService;
import com.pdd.user.service.UserService;
import com.pdd.user.util.JwtUtil;
import com.pdd.user.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 尝试通过用户名、手机号或邮箱查找用户
        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByPhone(loginDTO.getUsername());
        }
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByEmail(loginDTO.getUsername());
        }

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = userOptional.get();
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userService.convertToVO(userService.convertToDTO(user)));

        return result;
    }

    @Override
    @Transactional
    public UserDTO register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检查手机号是否已存在
        if (userDTO.getPhone() != null && userRepository.existsByPhone(userDTO.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        // 检查邮箱是否已存在
        if (userDTO.getEmail() != null && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 创建用户
        return userService.createUser(userDTO);
    }

    @Override
    public String refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("无效的refresh token");
        }

        // 从refreshToken中获取用户名
        String username = jwtUtil.extractUsername(refreshToken);
        
        // 生成新的token
        return jwtUtil.generateToken(username);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public UserDTO getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("无效的token");
        }

        String username = jwtUtil.extractUsername(token);
        return userService.getUserByUsername(username);
    }

    // 实现UserDetailsService接口，用于Spring Security认证
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (!userOptional.isPresent()) {
                userOptional = userRepository.findByPhone(username);
            }
            if (!userOptional.isPresent()) {
                userOptional = userRepository.findByEmail(username);
            }
            
            if (!userOptional.isPresent()) {
                throw new UsernameNotFoundException("用户不存在");
            }

            User user = userOptional.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }
}