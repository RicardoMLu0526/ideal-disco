package com.pdd.user.vo;

import com.pdd.user.entity.Address;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressVOTest {

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

        // 转换为VO
        AddressVO vo = AddressVO.fromEntity(address);

        // 验证转换结果
        assertNotNull(vo);
        assertEquals(1L, vo.getId());
        assertEquals(1001L, vo.getUserId());
        assertEquals("张三", vo.getName());
        assertEquals("13800138000", vo.getPhone());
        assertEquals("广东省", vo.getProvince());
        assertEquals("深圳市", vo.getCity());
        assertEquals("南山区", vo.getDistrict());
        assertEquals("科技园路1号", vo.getDetail());
        assertEquals(1, vo.getIsDefault());
    }

    @Test
    void testFromEntityWithNull() {
        // 测试空实体转换
        AddressVO vo = AddressVO.fromEntity(null);
        assertNull(vo);
    }

    @Test
    void testSettersAndGetters() {
        // 测试setter和getter方法
        AddressVO vo = new AddressVO();
        vo.setId(2L);
        vo.setUserId(1002L);
        vo.setName("李四");
        vo.setPhone("13900139000");
        vo.setProvince("北京市");
        vo.setCity("北京市");
        vo.setDistrict("朝阳区");
        vo.setDetail("建国路88号");
        vo.setIsDefault(0);

        assertEquals(2L, vo.getId());
        assertEquals(1002L, vo.getUserId());
        assertEquals("李四", vo.getName());
        assertEquals("13900139000", vo.getPhone());
        assertEquals("北京市", vo.getProvince());
        assertEquals("北京市", vo.getCity());
        assertEquals("朝阳区", vo.getDistrict());
        assertEquals("建国路88号", vo.getDetail());
        assertEquals(0, vo.getIsDefault());
    }
}