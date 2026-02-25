package com.pdd.order.service.impl;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.feign.ProductServiceFeign;
import com.pdd.order.feign.StockServiceFeign;
import com.pdd.order.mapper.OrderMapper;
import com.pdd.order.mapper.OrderItemMapper;
import com.pdd.order.service.OrderService;
import com.pdd.order.util.OrderNoGenerator;
import com.pdd.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductServiceFeign productServiceFeign;

    @Autowired
    private StockServiceFeign stockServiceFeign;

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        // 1. 从数据库获取订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 获取订单项信息
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);

        // 3. 构建返回结果
        OrderVO orderVO = new OrderVO();
        orderVO.setId(order.getId());
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setUserId(order.getUserId());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setActualAmount(order.getActualAmount());
        orderVO.setPaymentStatus(order.getPaymentStatus());
        orderVO.setOrderStatus(order.getOrderStatus());
        orderVO.setShippingAddress(order.getShippingAddress());
        orderVO.setReceiverName(order.getReceiverName());
        orderVO.setReceiverPhone(order.getReceiverPhone());
        orderVO.setItems(orderItems);
        return orderVO;
    }

    @Override
    public List<Order> getOrderList() {
        return orderMapper.selectList(null);
    }

    @Override
    public Order createOrder(OrderCreateDTO createDTO) {
        // 1. 生成订单号
        String orderNo = OrderNoGenerator.generate();

        // 2. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 3. 处理订单项
        for (OrderItemDTO itemDTO : createDTO.getItems()) {
            // 调用商品服务获取商品信息
            Map<String, Object> productInfo = productServiceFeign.getProductById(itemDTO.getProductId());
            if (productInfo == null || productInfo.get("data") == null) {
                throw new RuntimeException("商品不存在");
            }
            
            Map<String, Object> productData = (Map<String, Object>) productInfo.get("data");
            BigDecimal price = new BigDecimal(productData.get("price").toString());
            String productName = productData.get("name").toString();
            
            // 检查库存
            Integer stock = productServiceFeign.getProductStock(itemDTO.getProductId());
            if (stock == null || stock < itemDTO.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 扣减库存
            Map<String, Object> stockRequest = new HashMap<>();
            stockRequest.put("productId", itemDTO.getProductId());
            stockRequest.put("quantity", itemDTO.getQuantity());
            stockServiceFeign.decreaseStock(stockRequest);
            
            // 计算金额
            BigDecimal itemAmount = price.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(price);
            orderItem.setProductName(productName);
            orderItems.add(orderItem);
        }

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(createDTO.getUserId());
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount); // 简化处理，实际应该考虑优惠券等
        order.setOrderStatus(0); // 待付款
        order.setPaymentStatus(0); // 未支付
        order.setReceiverName(createDTO.getReceiverName());
        order.setReceiverPhone(createDTO.getReceiverPhone());
        order.setShippingAddress(createDTO.getShippingAddress());
        orderMapper.insert(order);

        // 5. 批量插入订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }

        return order;
    }

    @Override
    public Order updateOrder(Long orderId, Order order) {
        order.setId(orderId);
        orderMapper.updateById(order);
        return order;
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderMapper.deleteById(orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(5); // 已取消
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 4. 释放库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            // 调用库存服务释放库存
            Map<String, Object> stockRequest = new HashMap<>();
            stockRequest.put("productId", item.getProductId());
            stockRequest.put("quantity", item.getQuantity());
            stockServiceFeign.increaseStock(stockRequest);
            log.info("释放库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public void payOrder(Long orderId, String paymentMethod) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 调用支付服务创建支付记录
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", orderId);
        paymentRequest.put("amount", order.getActualAmount());
        paymentRequest.put("paymentMethod", paymentMethod);
        paymentRequest.put("orderNo", order.getOrderNo());
        // 实际应该调用支付服务
        log.info("创建支付记录: orderId={}, amount={}, paymentMethod={}", orderId, order.getActualAmount(), paymentMethod);

        // 4. 更新订单状态
        order.setOrderStatus(1); // 待发货
        order.setPaymentStatus(1); // 已支付
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 5. 确认扣减库存（在创建订单时已经扣减，这里可以做确认操作）
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            log.info("确认扣减库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public void confirmOrder(Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 2) { // 不是待收货状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(4); // 已完成
        order.setConfirmTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void shipOrder(Long orderId, String trackingNumber) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 1) { // 不是待发货状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(2); // 待收货
        order.setShippingTime(LocalDateTime.now());
        order.setTrackingNumber(trackingNumber);
        log.info("订单发货: orderId={}, trackingNumber={}", orderId, trackingNumber);
        orderMapper.updateById(order);
    }
}
