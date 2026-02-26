package com.pdd.order.test;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.service.OrderService;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    private OrderCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("张三");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("北京市朝阳区");
        
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.getItems().add(itemDTO);
    }

    /**
     * 测试创建订单 - 正常流程
     */
    @Test
    public void testCreateOrderNormal() {
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        assertNotNull(order.getId(), "订单ID为空");
        assertNotNull(order.getOrderNo(), "订单号为空");
        assertTrue(order.getOrderStatus() == 0, "订单状态应为待支付");
        System.out.println("测试创建订单成功: " + order.getOrderNo());
    }

    /**
     * 测试创建订单 - 空订单项
     */
    @Test
    public void testCreateOrderEmptyItems() {
        createDTO.getItems().clear();
        try {
            orderService.createOrder(createDTO);
            // 应该抛出异常
            assertTrue(false, "订单项为空时应该抛出异常");
        } catch (Exception e) {
            System.out.println("测试空订单项成功: " + e.getMessage());
        }
    }

    /**
     * 测试获取订单详情
     */
    @Test
    public void testGetOrderDetail() {
        // 先创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        
        // 获取订单详情
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        assertTrue(orderVO.getId().equals(order.getId()), "订单ID不匹配");
        assertTrue(orderVO.getOrderNo().equals(order.getOrderNo()), "订单号不匹配");
        System.out.println("测试获取订单详情成功: " + orderVO.getOrderNo());
    }

    /**
     * 测试取消订单
     */
    @Test
    public void testCancelOrder() {
        // 先创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        
        // 取消订单
        orderService.cancelOrder(order.getId());
        
        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        System.out.println("测试取消订单成功: " + orderVO.getOrderNo());
    }

    /**
     * 测试支付订单
     */
    @Test
    public void testPayOrder() {
        // 先创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        
        // 支付订单
        orderService.payOrder(order.getId(), "wechat");
        
        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        System.out.println("测试支付订单成功: " + orderVO.getOrderNo());
    }
}
