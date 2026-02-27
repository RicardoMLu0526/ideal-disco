package com.pdd.user.controller;

import com.pdd.user.common.Result;
import com.pdd.user.dto.LoginDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = authService.login(loginDTO);
        return Result.success("登录成功", result);
    }

    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody UserDTO userDTO) {
        UserDTO result = authService.register(userDTO);
        return Result.success("注册成功", result);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return Result.success("登出成功");
    }

    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newToken = authService.refreshToken(refreshToken);
        return Result.success("Token刷新成功", newToken);
    }

    @PostMapping("/validate")
    public Result<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            UserDTO user = authService.getUserFromToken(token);
            return Result.success("Token有效", Map.of("valid", true, "user", user));
        } else {
            return Result.error(401, "Token无效或已过期");
        }
    }
}
