package com.pdd.user.service;

import com.pdd.user.dto.UserDTO;
import com.pdd.user.vo.UserVO;
import com.pdd.user.entity.User;
import com.pdd.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser1");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138001");
        userDTO.setEmail("test1@example.com");
        userDTO.setNickname("Test User 1");
        userDTO.setStatus(1);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertEquals("testuser1", result.getUsername());
        assertEquals("13800138001", result.getPhone());
        assertEquals("test1@example.com", result.getEmail());
    }

    @Test
    void testGetUserById() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser2");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138002");

        UserDTO created = userService.createUser(userDTO);
        UserDTO result = userService.getUserById(created.getUserId());

        assertNotNull(result);
        assertEquals(created.getUserId(), result.getUserId());
        assertEquals("testuser2", result.getUsername());
    }

    @Test
    void testGetUserByUsername() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser3");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138003");

        userService.createUser(userDTO);
        UserDTO result = userService.getUserByUsername("testuser3");

        assertNotNull(result);
        assertEquals("testuser3", result.getUsername());
    }

    @Test
    void testUpdateUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser4");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138004");
        userDTO.setNickname("Original Nickname");

        UserDTO created = userService.createUser(userDTO);

        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("testuser4");
        updateDTO.setPhone("13800138004");
        updateDTO.setNickname("Updated Nickname");

        UserDTO result = userService.updateUser(created.getUserId(), updateDTO);

        assertNotNull(result);
        assertEquals("Updated Nickname", result.getNickname());
    }

    @Test
    void testDeleteUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser5");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138005");

        UserDTO created = userService.createUser(userDTO);
        Long userId = created.getUserId();

        userService.deleteUser(userId);

        UserDTO result = userService.getUserById(userId);
        assertNull(result);
    }

    @Test
    void testConvertToVO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setUsername("testuser");
        userDTO.setPhone("13800138000");
        userDTO.setEmail("test@example.com");

        UserVO userVO = userService.convertToVO(userDTO);

        assertNotNull(userVO);
        assertEquals(userDTO.getUserId(), userVO.getUserId());
        assertEquals(userDTO.getUsername(), userVO.getUsername());
        assertEquals(userDTO.getPhone(), userVO.getPhone());
    }

    @Test
    void testDuplicateUsername() {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setUsername("duplicate");
        userDTO1.setPassword("password123");
        userDTO1.setPhone("13800138011");

        userService.createUser(userDTO1);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setUsername("duplicate");
        userDTO2.setPassword("password123");
        userDTO2.setPhone("13800138012");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDTO2);
        });
    }
}