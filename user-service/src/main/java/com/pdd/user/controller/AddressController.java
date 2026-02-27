package com.pdd.user.controller;

import com.pdd.user.common.Result;
import com.pdd.user.dto.AddressDTO;
import com.pdd.user.service.AddressService;
import com.pdd.user.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/{addressId}")
    public Result<AddressVO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        if (addressDTO == null) {
            return Result.error("地址不存在");
        }
        AddressVO addressVO = addressService.convertToVO(addressDTO);
        return Result.success(addressVO);
    }

    @PostMapping
    public Result<AddressVO> createAddress(@RequestBody AddressDTO addressDTO) {
        AddressDTO result = addressService.createAddress(addressDTO);
        AddressVO addressVO = addressService.convertToVO(result);
        return Result.success("地址创建成功", addressVO);
    }

    @PutMapping("/{addressId}")
    public Result<AddressVO> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        AddressDTO result = addressService.updateAddress(addressId, addressDTO);
        AddressVO addressVO = addressService.convertToVO(result);
        return Result.success("地址更新成功", addressVO);
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return Result.success("地址删除成功");
    }

    @PutMapping("/{userId}/{addressId}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return Result.success("默认地址设置成功");
    }
}
