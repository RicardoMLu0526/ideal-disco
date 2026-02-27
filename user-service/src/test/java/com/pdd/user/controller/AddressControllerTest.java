package com.pdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.user.dto.AddressDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.AddressService;
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
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    private Long createTestUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("addresstest" + System.currentTimeMillis());
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138070");
        UserDTO created = userService.createUser(userDTO);
        return created.getUserId();
    }

    @Test
    void testGetAddressById() throws Exception {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("张三");
        addressDTO.setPhone("13800138071");
        addressDTO.setProvince("广东省");
        addressDTO.setCity("深圳市");
        addressDTO.setDistrict("南山区");
        addressDTO.setDetail("科技园路1号");
        AddressDTO created = addressService.createAddress(addressDTO);

        mockMvc.perform(get("/api/users/addresses/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(created.getId()))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void testCreateAddress() throws Exception {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("王五");
        addressDTO.setPhone("13800138073");
        addressDTO.setProvince("上海市");
        addressDTO.setCity("上海市");
        addressDTO.setDistrict("浦东新区");
        addressDTO.setDetail("世纪大道1号");
        addressDTO.setIsDefault(1);

        mockMvc.perform(post("/api/users/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("地址创建成功"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("王五"));
    }

    @Test
    void testUpdateAddress() throws Exception {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("赵六");
        addressDTO.setPhone("13800138074");
        addressDTO.setProvince("广州市");
        addressDTO.setCity("广州市");
        addressDTO.setDistrict("天河区");
        addressDTO.setDetail("天河路1号");
        AddressDTO created = addressService.createAddress(addressDTO);

        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setUserId(userId);
        updateDTO.setName("赵六更新");
        updateDTO.setPhone("13800138075");
        updateDTO.setProvince("广州市");
        updateDTO.setCity("广州市");
        updateDTO.setDistrict("天河区");
        updateDTO.setDetail("天河路2号");

        mockMvc.perform(put("/api/users/addresses/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("地址更新成功"))
                .andExpect(jsonPath("$.data.name").value("赵六更新"));
    }

    @Test
    void testDeleteAddress() throws Exception {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("孙七");
        addressDTO.setPhone("13800138076");
        addressDTO.setProvince("深圳市");
        addressDTO.setCity("深圳市");
        addressDTO.setDistrict("南山区");
        addressDTO.setDetail("科技园路1号");
        AddressDTO created = addressService.createAddress(addressDTO);

        mockMvc.perform(delete("/api/users/addresses/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("地址删除成功"));
    }

    @Test
    void testSetDefaultAddress() throws Exception {
        Long userId = createTestUser();

        AddressDTO address1 = new AddressDTO();
        address1.setUserId(userId);
        address1.setName("默认地址1");
        address1.setPhone("13800138077");
        address1.setProvince("广东省");
        address1.setCity("深圳市");
        address1.setDistrict("南山区");
        address1.setDetail("科技园路1号");
        address1.setIsDefault(1);
        AddressDTO created1 = addressService.createAddress(address1);

        AddressDTO address2 = new AddressDTO();
        address2.setUserId(userId);
        address2.setName("默认地址2");
        address2.setPhone("13800138078");
        address2.setProvince("广东省");
        address2.setCity("深圳市");
        address2.setDistrict("福田区");
        address2.setDetail("中心城1号");
        AddressDTO created2 = addressService.createAddress(address2);

        mockMvc.perform(put("/api/users/" + userId + "/addresses/" + created2.getId() + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("默认地址设置成功"));
    }
}
