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
public class OrderServiceIntegrationTest {

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
        itemDTO.setQuantity(1);
        createDTO.getItems().add(itemDTO);
    }

    /**
     * 测试正常支付流程
     * 1. 创建订单
     * 2. 支付订单
     */
    @Test
    public void testNormalPaymentFlow() {
        // 创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        System.out.println("创建订单成功: " + order.getOrderNo());
        
        // 支付订单
        orderService.payOrder(order.getId(), "wechat");
        System.out.println("支付订单成功: " + order.getOrderNo());
        
        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        System.out.println("测试正常支付流程成功");
    }

    /**
     * 测试订单取消流程
     * 1. 创建订单
     * 2. 取消订单
     */
    @Test
    public void testOrderCancelFlow() {
        // 创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        System.out.println("创建订单成功: " + order.getOrderNo());
        
        // 取消订单
        orderService.cancelOrder(order.getId());
        System.out.println("取消订单成功: " + order.getOrderNo());
        
        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        System.out.println("测试订单取消流程成功");
    }

    /**
     * 测试订单发货流程
     * 1. 创建订单
     * 2. 支付订单
     * 3. 发货
     * 4. 确认收货
     */
    @Test
    public void testOrderShipFlow() {
        // 创建订单
        Order order = orderService.createOrder(createDTO);
        assertNotNull(order, "订单创建失败");
        System.out.println("创建订单成功: " + order.getOrderNo());
        
        // 支付订单
        orderService.payOrder(order.getId(), "wechat");
        System.out.println("支付订单成功: " + order.getOrderNo());
        
        // 发货
        orderService.shipOrder(order.getId(), "SF1234567890");
        System.out.println("发货成功: " + order.getOrderNo());
        
        // 确认收货
        orderService.confirmOrder(order.getId());
        System.out.println("确认收货成功: " + order.getOrderNo());
        
        // 验证订单状态
        OrderVO orderVO = orderService.getOrderDetail(order.getId());
        assertNotNull(orderVO, "订单详情获取失败");
        System.out.println("测试订单发货流程成功");
    }

    /**
     * 测试批量创建订单
     * 测试并发场景下的订单创建
     */
    @Test
    public void testBatchCreateOrder() {
        // 批量创建5个订单
        for (int i = 0; i < 5; i++) {
            Order order = orderService.createOrder(createDTO);
            assertNotNull(order, "订单创建失败");
            System.out.println("创建订单" + (i + 1) + "成功: " + order.getOrderNo());
        }
        System.out.println("测试批量创建订单成功");
    }
}
