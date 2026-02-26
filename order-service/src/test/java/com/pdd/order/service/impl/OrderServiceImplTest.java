package com.pdd.order.service.impl;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.feign.ProductServiceFeign;
import com.pdd.order.feign.StockServiceFeign;
import com.pdd.order.mapper.OrderMapper;
import com.pdd.order.mapper.OrderItemMapper;
import com.pdd.order.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductServiceFeign productServiceFeign;

    @Mock
    private StockServiceFeign stockServiceFeign;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // 准备测试数据
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setReceiverName("测试用户");
        createDTO.setReceiverPhone("13800138000");
        createDTO.setShippingAddress("测试地址");

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        createDTO.setItems(List.of(itemDTO));

        // 模拟商品服务返回
        Map<String, Object> productInfo = Map.of(
                "code", 200,
                "data", Map.of(
                        "id", 1L,
                        "name", "测试商品",
                        "price", 100.0
                )
        );
        when(productServiceFeign.getProductById(1L)).thenReturn(productInfo);
        when(productServiceFeign.getProductStock(1L)).thenReturn(10);

        // 模拟库存服务
        when(stockServiceFeign.decreaseStock(anyMap())).thenReturn(Map.of("code", 200));

        // 模拟订单插入
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20240101000000");
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return 1;
        });

        // 执行测试
        Order result = orderService.createOrder(createDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderMapper, times(1)).insert(any(Order.class));
        verify(orderItemMapper, times(1)).insert(any(OrderItem.class));
    }

    @Test
    void testGetOrderDetail() {
        // 准备测试数据
        Long orderId = 1L;

        // 模拟订单数据
        Order order = new Order();
        order.setId(orderId);
        order.setOrderNo("20240101000000");
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal(200));
        order.setActualAmount(new BigDecimal(200));
        order.setPaymentStatus(1);
        order.setOrderStatus(1);
        order.setShippingAddress("测试地址");
        order.setReceiverName("测试用户");
        order.setReceiverPhone("13800138000");

        // 模拟订单项数据
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal(100));
        orderItem.setProductName("测试商品");

        // 模拟 Mapper 方法
        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderItemMapper.selectByOrderId(orderId)).thenReturn(List.of(orderItem));

        // 执行测试
        OrderVO result = orderService.getOrderDetail(orderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testCancelOrder() {
        // 准备测试数据
        Long orderId = 1L;

        // 模拟订单数据
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(0); // 待付款

        // 模拟订单项数据
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        // 模拟 Mapper 方法
        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderItemMapper.selectByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(stockServiceFeign.increaseStock(anyMap())).thenReturn(Map.of("code", 200));

        // 执行测试
        orderService.cancelOrder(orderId);

        // 验证结果
        assertEquals(5, order.getOrderStatus()); // 已取消
        verify(orderMapper, times(1)).updateById(order);
    }

    @Test
    void testPayOrder() {
        // 准备测试数据
        Long orderId = 1L;
        String paymentMethod = "alipay";

        // 模拟订单数据
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(0); // 待付款
        order.setActualAmount(new BigDecimal(200));
        order.setOrderNo("20240101000000");

        // 模拟 Mapper 方法
        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试
        orderService.payOrder(orderId, paymentMethod);

        // 验证结果
        assertEquals(1, order.getOrderStatus()); // 待发货
        assertEquals(1, order.getPaymentStatus()); // 已支付
        verify(orderMapper, times(1)).updateById(order);
    }

    @Test
    void testShipOrder() {
        // 准备测试数据
        Long orderId = 1L;
        String trackingNumber = "SF1234567890";

        // 模拟订单数据
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(1); // 待发货

        // 模拟 Mapper 方法
        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试
        orderService.shipOrder(orderId, trackingNumber);

        // 验证结果
        assertEquals(2, order.getOrderStatus()); // 待收货
        assertEquals(trackingNumber, order.getTrackingNumber());
        verify(orderMapper, times(1)).updateById(order);
    }

    @Test
    void testConfirmOrder() {
        // 准备测试数据
        Long orderId = 1L;

        // 模拟订单数据
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(2); // 待收货

        // 模拟 Mapper 方法
        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试
        orderService.confirmOrder(orderId);

        // 验证结果
        assertEquals(4, order.getOrderStatus()); // 已完成
        verify(orderMapper, times(1)).updateById(order);
    }
}
