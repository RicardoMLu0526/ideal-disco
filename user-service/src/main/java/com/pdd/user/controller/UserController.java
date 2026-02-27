package com.pdd.user.controller;

import com.pdd.user.common.Result;
import com.pdd.user.dto.AddressDTO;
import com.pdd.user.dto.UserDTO;
import com.pdd.user.service.AddressService;
import com.pdd.user.service.UserService;
import com.pdd.user.vo.AddressVO;
import com.pdd.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            return Result.error("用户不存在");
        }
        UserVO userVO = userService.convertToVO(userDTO);
        return Result.success(userVO);
    }

    @GetMapping("/{userId}/addresses")
    public Result<List<AddressVO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addressDTOList = addressService.getAddressesByUserId(userId);
        List<AddressVO> addressVOList = addressDTOList.stream()
                .map(addressService::convertToVO)
                .collect(Collectors.toList());
        return Result.success(addressVOList);
    }

    @GetMapping("/username/{username}")
    public Result<UserVO> getUserByUsername(@PathVariable String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        if (userDTO == null) {
            return Result.error("用户不存在");
        }
        UserVO userVO = userService.convertToVO(userDTO);
        return Result.success(userVO);
    }

    @GetMapping("/phone/{phone}")
    public Result<UserVO> getUserByPhone(@PathVariable String phone) {
        UserDTO userDTO = userService.getUserByPhone(phone);
        if (userDTO == null) {
            return Result.error("用户不存在");
        }
        UserVO userVO = userService.convertToVO(userDTO);
        return Result.success(userVO);
    }

    @GetMapping("/email/{email}")
    public Result<UserVO> getUserByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.getUserByEmail(email);
        if (userDTO == null) {
            return Result.error("用户不存在");
        }
        UserVO userVO = userService.convertToVO(userDTO);
        return Result.success(userVO);
    }

    @GetMapping
    public Result<List<UserVO>> getAllUsers() {
        List<UserDTO> userDTOList = userService.getAllUsers();
        List<UserVO> userVOList = userDTOList.stream()
                .map(userService::convertToVO)
                .collect(Collectors.toList());
        return Result.success(userVOList);
    }

    @PostMapping
    public Result<UserVO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO result = userService.createUser(userDTO);
        UserVO userVO = userService.convertToVO(result);
        return Result.success("用户创建成功", userVO);
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO result = userService.updateUser(id, userDTO);
        UserVO userVO = userService.convertToVO(result);
        return Result.success("用户更新成功", userVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功");
    }
}
