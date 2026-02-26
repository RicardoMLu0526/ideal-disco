package com.pdd.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> selectByUserId(@Param("userId") Long userId);
    Order selectByOrderNo(@Param("orderNo") String orderNo);
}
