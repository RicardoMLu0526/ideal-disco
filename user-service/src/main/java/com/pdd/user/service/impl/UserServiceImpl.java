package com.pdd.user.service.impl;

import com.pdd.user.dto.UserDTO;
import com.pdd.user.vo.UserVO;
import com.pdd.user.entity.User;
import com.pdd.user.repository.UserRepository;
import com.pdd.user.service.UserService;
import com.pdd.user.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public UserDTO getUserByPhone(String phone) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        return userOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
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

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setNickname(userDTO.getNickname());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOptional.get();
        
        // 检查用户名是否已被其他用户使用
        if (!user.getUsername().equals(userDTO.getUsername()) && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检查手机号是否已被其他用户使用
        if (userDTO.getPhone() != null && !user.getPhone().equals(userDTO.getPhone()) && userRepository.existsByPhone(userDTO.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        // 检查邮箱是否已被其他用户使用
        if (userDTO.getEmail() != null && !user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        user.setUsername(userDTO.getUsername());
        if (userDTO.getPassword() != null) {
            user.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        }
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setNickname(userDTO.getNickname());
        user.setAvatar(userDTO.getAvatar());
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        return dto;
    }

    @Override
    public UserVO convertToVO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setUserId(userDTO.getUserId());
        vo.setUsername(userDTO.getUsername());
        vo.setPhone(userDTO.getPhone());
        vo.setEmail(userDTO.getEmail());
        vo.setNickname(userDTO.getNickname());
        vo.setAvatar(userDTO.getAvatar());
        vo.setStatus(userDTO.getStatus());
        return vo;
    }
}