package com.pdd.user.service;

import com.pdd.user.dto.AddressDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.vo.AddressVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    private Long createTestUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("addresstest" + System.currentTimeMillis());
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138030");
        UserDTO created = userService.createUser(userDTO);
        return created.getUserId();
    }

    @Test
    void testCreateAddress() {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("张三");
        addressDTO.setPhone("13800138031");
        addressDTO.setProvince("广东省");
        addressDTO.setCity("深圳市");
        addressDTO.setDistrict("南山区");
        addressDTO.setDetail("科技园路1号");
        addressDTO.setIsDefault(1);

        AddressDTO result = addressService.createAddress(addressDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("张三", result.getName());
        assertEquals("广东省", result.getProvince());
    }

    @Test
    void testGetAddressById() {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("李四");
        addressDTO.setPhone("13800138032");
        addressDTO.setProvince("北京市");
        addressDTO.setCity("北京市");
        addressDTO.setDistrict("朝阳区");
        addressDTO.setDetail("建国路88号");

        AddressDTO created = addressService.createAddress(addressDTO);
        AddressDTO result = addressService.getAddressById(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("李四", result.getName());
    }

    @Test
    void testGetAddressesByUserId() {
        Long userId = createTestUser();

        AddressDTO address1 = new AddressDTO();
        address1.setUserId(userId);
        address1.setName("王五");
        address1.setPhone("13800138033");
        address1.setProvince("上海市");
        address1.setCity("上海市");
        address1.setDistrict("浦东新区");
        address1.setDetail("世纪大道100号");
        addressService.createAddress(address1);

        AddressDTO address2 = new AddressDTO();
        address2.setUserId(userId);
        address2.setName("赵六");
        address2.setPhone("13800138034");
        address2.setProvince("广州市");
        address2.setCity("广州市");
        address2.setDistrict("天河区");
        address2.setDetail("天河路100号");
        addressService.createAddress(address2);

        List<AddressDTO> result = addressService.getAddressesByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetDefaultAddressByUserId() {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("孙七");
        addressDTO.setPhone("13800138035");
        addressDTO.setProvince("浙江省");
        addressDTO.setCity("杭州市");
        addressDTO.setDistrict("西湖区");
        addressDTO.setDetail("西湖大道1号");
        addressDTO.setIsDefault(1);

        addressService.createAddress(addressDTO);
        AddressDTO result = addressService.getDefaultAddressByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.getIsDefault());
    }

    @Test
    void testUpdateAddress() {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("周八");
        addressDTO.setPhone("13800138036");
        addressDTO.setProvince("江苏省");
        addressDTO.setCity("南京市");
        addressDTO.setDistrict("鼓楼区");
        addressDTO.setDetail("中山路1号");

        AddressDTO created = addressService.createAddress(addressDTO);

        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setUserId(userId);
        updateDTO.setName("周八更新");
        updateDTO.setPhone("13800138037");
        updateDTO.setProvince("江苏省");
        updateDTO.setCity("南京市");
        updateDTO.setDistrict("玄武区");
        updateDTO.setDetail("新街口1号");

        AddressDTO result = addressService.updateAddress(created.getId(), updateDTO);

        assertNotNull(result);
        assertEquals("周八更新", result.getName());
        assertEquals("13800138037", result.getPhone());
    }

    @Test
    void testDeleteAddress() {
        Long userId = createTestUser();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUserId(userId);
        addressDTO.setName("吴九");
        addressDTO.setPhone("13800138038");
        addressDTO.setProvince("四川省");
        addressDTO.setCity("成都市");
        addressDTO.setDistrict("锦江区");
        addressDTO.setDetail("春熙路1号");

        AddressDTO created = addressService.createAddress(addressDTO);
        Long addressId = created.getId();

        addressService.deleteAddress(addressId);

        AddressDTO result = addressService.getAddressById(addressId);
        assertNull(result);
    }

    @Test
    void testSetDefaultAddress() {
        Long userId = createTestUser();

        AddressDTO address1 = new AddressDTO();
        address1.setUserId(userId);
        address1.setName("郑十");
        address1.setPhone("13800138039");
        address1.setProvince("湖北省");
        address1.setCity("武汉市");
        address1.setDistrict("江汉区");
        address1.setDetail("解放大道1号");
        address1.setIsDefault(1);
        AddressDTO created1 = addressService.createAddress(address1);

        AddressDTO address2 = new AddressDTO();
        address2.setUserId(userId);
        address2.setName("钱十一");
        address2.setPhone("13800138040");
        address2.setProvince("湖南省");
        address2.setCity("长沙市");
        address2.setDistrict("岳麓区");
        address2.setDetail("麓山路1号");
        AddressDTO created2 = addressService.createAddress(address2);

        addressService.setDefaultAddress(userId, created2.getId());

        AddressDTO defaultAddress = addressService.getDefaultAddressByUserId(userId);
        assertNotNull(defaultAddress);
        assertEquals(created2.getId(), defaultAddress.getId());
    }

    @Test
    void testConvertToVO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(1L);
        addressDTO.setUserId(100L);
        addressDTO.setName("测试");
        addressDTO.setPhone("13800138041");
        addressDTO.setProvince("广东省");
        addressDTO.setCity("深圳市");
        addressDTO.setDistrict("南山区");
        addressDTO.setDetail("科技园路1号");
        addressDTO.setIsDefault(1);

        AddressVO addressVO = addressService.convertToVO(addressDTO);

        assertNotNull(addressVO);
        assertEquals(addressDTO.getId(), addressVO.getId());
        assertEquals(addressDTO.getName(), addressVO.getName());
    }

    @Test
    void testOnlyOneDefaultAddress() {
        Long userId = createTestUser();

        AddressDTO address1 = new AddressDTO();
        address1.setUserId(userId);
        address1.setName("默认地址1");
        address1.setPhone("13800138042");
        address1.setProvince("广东省");
        address1.setCity("深圳市");
        address1.setDistrict("南山区");
        address1.setDetail("科技园路1号");
        address1.setIsDefault(1);
        addressService.createAddress(address1);

        AddressDTO address2 = new AddressDTO();
        address2.setUserId(userId);
        address2.setName("默认地址2");
        address2.setPhone("13800138043");
        address2.setProvince("广东省");
        address2.setCity("深圳市");
        address2.setDistrict("福田区");
        address2.setDetail("中心城1号");
        address2.setIsDefault(1);
        addressService.createAddress(address2);

        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        long defaultCount = addresses.stream()
                .filter(a -> a.getIsDefault() != null && a.getIsDefault() == 1)
                .count();

        assertEquals(1, defaultCount);
    }
}