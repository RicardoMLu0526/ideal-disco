package com.pdd.user.mapper;

import com.pdd.user.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    Address selectById(@Param("id") Long id);

    List<Address> selectByUserId(@Param("userId") Long userId);

    Address selectDefaultByUserId(@Param("userId") Long userId);

    int insert(Address address);

    int update(Address address);

    int deleteById(@Param("id") Long id);

    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int clearDefaultByUserId(@Param("userId") Long userId);
}
