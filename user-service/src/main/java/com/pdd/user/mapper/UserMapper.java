package com.pdd.user.mapper;

import com.pdd.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    User selectByPhone(@Param("phone") String phone);

    User selectByEmail(@Param("email") String email);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    boolean existsByUsername(@Param("username") String username);

    boolean existsByPhone(@Param("phone") String phone);

    boolean existsByEmail(@Param("email") String email);
}
