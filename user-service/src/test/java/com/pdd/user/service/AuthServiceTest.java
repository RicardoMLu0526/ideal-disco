package com.pdd.user.service;

import com.pdd.user.dto.LoginDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Test
    void testLogin() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("logintest");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138020");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("logintest");
        loginDTO.setPassword("password123");

        Map<String, Object> result = authService.login(loginDTO);

        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertNotNull(result.get("user"));
    }

    @Test
    void testLoginWithPhone() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("logintestphone");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138021");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("13800138021");
        loginDTO.setPassword("password123");

        Map<String, Object> result = authService.login(loginDTO);

        assertNotNull(result);
        assertNotNull(result.get("token"));
    }

    @Test
    void testLoginWithEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("logintestemail");
        userDTO.setPassword("password123");
        userDTO.setEmail("logintestemail@example.com");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("logintestemail@example.com");
        loginDTO.setPassword("password123");

        Map<String, Object> result = authService.login(loginDTO);

        assertNotNull(result);
        assertNotNull(result.get("token"));
    }

    @Test
    void testLoginWithWrongPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("wrongpwdtest");
        userDTO.setPassword("correctpassword");
        userDTO.setPhone("13800138022");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("wrongpwdtest");
        loginDTO.setPassword("wrongpassword");

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginDTO);
        });
    }

    @Test
    void testLoginWithNonExistentUser() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginDTO);
        });
    }

    @Test
    void testRegister() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138023");
        userDTO.setEmail("newuser@example.com");

        UserDTO result = authService.register(userDTO);

        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void testRegisterDuplicateUsername() {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setUsername("duplicateuser");
        userDTO1.setPassword("password123");
        userDTO1.setPhone("13800138024");
        authService.register(userDTO1);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setUsername("duplicateuser");
        userDTO2.setPassword("password123");
        userDTO2.setPhone("13800138025");

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(userDTO2);
        });
    }

    @Test
    void testValidateToken() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("validatetoken");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138026");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("validatetoken");
        loginDTO.setPassword("password123");

        Map<String, Object> result = authService.login(loginDTO);
        String token = (String) result.get("token");

        boolean isValid = authService.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void testValidateInvalidToken() {
        boolean isValid = authService.validateToken("invalid.token.here");
        assertFalse(isValid);
    }

    @Test
    void testGetUserFromToken() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("tokenuser");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138027");
        userService.createUser(userDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("tokenuser");
        loginDTO.setPassword("password123");

        Map<String, Object> loginResult = authService.login(loginDTO);
        String token = (String) loginResult.get("token");

        UserDTO userFromToken = authService.getUserFromToken(token);

        assertNotNull(userFromToken);
        assertEquals("tokenuser", userFromToken.getUsername());
    }
}