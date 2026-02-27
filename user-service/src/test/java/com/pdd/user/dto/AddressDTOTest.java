package com.pdd.user.dto;

import com.pdd.user.entity.Address;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressDTOTest {

    @Test
    void testFromEntity() {
        // 创建Address实体
        Address address = new Address();
        address.setId(1L);
        address.setUserId(1001L);
        address.setName("张三");
        address.setPhone("13800138000");
        address.setProvince("广东省");
        address.setCity("深圳市");
        address.setDistrict("南山区");
        address.setDetail("科技园路1号");
        address.setIsDefault(1);

        // 转换为DTO
        AddressDTO dto = AddressDTO.fromEntity(address);

        // 验证转换结果
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1001L, dto.getUserId());
        assertEquals("张三", dto.getName());
        assertEquals("13800138000", dto.getPhone());
        assertEquals("广东省", dto.getProvince());
        assertEquals("深圳市", dto.getCity());
        assertEquals("南山区", dto.getDistrict());
        assertEquals("科技园路1号", dto.getDetail());
        assertEquals(1, dto.getIsDefault());
    }

    @Test
    void testFromEntityWithNull() {
        // 测试空实体转换
        AddressDTO dto = AddressDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        // 测试setter和getter方法
        AddressDTO dto = new AddressDTO();
        dto.setId(2L);
        dto.setUserId(1002L);
        dto.setName("李四");
        dto.setPhone("13900139000");
        dto.setProvince("北京市");
        dto.setCity("北京市");
        dto.setDistrict("朝阳区");
        dto.setDetail("建国路88号");
        dto.setIsDefault(0);

        assertEquals(2L, dto.getId());
        assertEquals(1002L, dto.getUserId());
        assertEquals("李四", dto.getName());
        assertEquals("13900139000", dto.getPhone());
        assertEquals("北京市", dto.getProvince());
        assertEquals("北京市", dto.getCity());
        assertEquals("朝阳区", dto.getDistrict());
        assertEquals("建国路88号", dto.getDetail());
        assertEquals(0, dto.getIsDefault());
    }
}