package com.pdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.user.dto.LoginDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    void testLogin() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("logintestcontroller");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138050");
        authService.register(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("logintestcontroller");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user").exists());
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("wrongpwdtest");
        userDTO.setPassword("correctpassword");
        userDTO.setPhone("13800138051");
        authService.register(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("wrongpwdtest");
        loginDTO.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testRegister() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newusercontroller");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138052");
        userDTO.setEmail("newusercontroller@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    void testVerifyTokenWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/verify")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Token无效或已过期"));
    }
}
