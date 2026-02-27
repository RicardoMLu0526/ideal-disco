package com.pdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.UserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    void testGetUserById() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("useridtest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138060");
        UserDTO created = userService.createUser(userDTO);

        mockMvc.perform(get("/api/users/" + created.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(created.getUserId()))
                .andExpect(jsonPath("$.data.username").value("useridtest"));
    }

    @Test
    void testGetUserByUsername() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("usernametest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138061");
        userService.createUser(userDTO);

        mockMvc.perform(get("/api/users/username/usernametest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("usernametest"));
    }

    @Test
    void testGetUserByPhone() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("phonetest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138062");
        userService.createUser(userDTO);

        mockMvc.perform(get("/api/users/phone/13800138062"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138062"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("createusertest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138063");
        userDTO.setEmail("createusertest@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.username").value("createusertest"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("updatetest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138064");
        userDTO.setNickname("原昵称");
        UserDTO created = userService.createUser(userDTO);

        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updatetest");
        updateDTO.setPhone("13800138064");
        updateDTO.setNickname("新昵称");

        mockMvc.perform(put("/api/users/" + created.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户更新成功"))
                .andExpect(jsonPath("$.data.nickname").value("新昵称"));
    }

    @Test
    void testDeleteUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("deletetest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138065");
        UserDTO created = userService.createUser(userDTO);

        mockMvc.perform(delete("/api/users/" + created.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户删除成功"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setUsername("alluser1");
        userDTO1.setPassword("password123");
        userDTO1.setPhone("13800138066");
        userService.createUser(userDTO1);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setUsername("alluser2");
        userDTO2.setPassword("password123");
        userDTO2.setPhone("13800138067");
        userService.createUser(userDTO2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}
