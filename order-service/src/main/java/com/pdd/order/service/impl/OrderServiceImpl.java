package com.pdd.order.service.impl;

import com.pdd.order.dto.OrderCreateDTO;
import com.pdd.order.dto.OrderItemDTO;
import com.pdd.order.entity.Order;
import com.pdd.order.entity.OrderItem;
import com.pdd.order.feign.ProductServiceFeign;
import com.pdd.order.feign.StockServiceFeign;
import com.pdd.order.mapper.OrderMapper;
import com.pdd.order.mapper.OrderItemMapper;
import com.pdd.order.service.InventoryService;
import com.pdd.order.service.OrderService;
import com.pdd.order.util.IdGenerator;
import com.pdd.order.util.OrderNoGenerator;
import com.pdd.order.vo.OrderVO;
import io.seata.spring.annotation.GlobalTransactional;
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

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private IdGenerator idGenerator;

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
    public OrderVO getOrderDetailByOrderNo(String orderNo) {
        // 1. 从数据库根据订单号获取订单信息
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 获取订单项信息
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());

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
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO createDTO) {
        // 1. 生成订单ID和订单号
        Long orderId = idGenerator.nextId();
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
            
            // 预扣库存
            boolean success = inventoryService.preDeductStock(itemDTO.getProductId(), itemDTO.getQuantity());
            if (!success) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 计算金额
            BigDecimal itemAmount = price.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setId(idGenerator.nextId());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(price);
            orderItem.setProductName(productName);
            orderItems.add(orderItem);
        }

        // 4. 创建订单
        Order order = new Order();
        order.setId(orderId);
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
    @GlobalTransactional(name = "cancel-order", rollbackFor = Exception.class)
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
            // 释放库存
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            log.info("释放库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }
    }

    @Override
    @GlobalTransactional(name = "cancel-order-by-no", rollbackFor = Exception.class)
    public void cancelOrderByOrderNo(String orderNo) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
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
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            // 释放库存
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            log.info("释放库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }
    }

    @Override
    @GlobalTransactional(name = "pay-order", rollbackFor = Exception.class)
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

        // 5. 确认扣减库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            inventoryService.confirmDeductStock(item.getProductId(), item.getQuantity());
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
    public void confirmOrderByOrderNo(String orderNo) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
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
    public void updateOrderStatus(String orderNo, Integer status, Long paymentId, LocalDateTime payTime) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 更新订单状态
        order.setOrderStatus(status);
        order.setPaymentStatus(1); // 已支付
        order.setPaymentId(paymentId);
        order.setPaymentTime(payTime);
        orderMapper.updateById(order);

        // 3. 如果是已支付状态，确认扣减库存
        if (status == 1) { // 已支付
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                inventoryService.confirmDeductStock(item.getProductId(), item.getQuantity());
                log.info("确认扣减库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            }
        }
    }

    @Override
    @GlobalTransactional(name = "handle-payment-success", rollbackFor = Exception.class)
    public void handlePaymentSuccess(String orderNo, String paymentNo, BigDecimal amount, LocalDateTime payTime, Integer paymentMethod) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(1); // 已支付
        order.setPaymentStatus(1); // 已支付
        order.setPaymentTime(payTime);
        orderMapper.updateById(order);

        // 4. 确认扣减库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            inventoryService.confirmDeductStock(item.getProductId(), item.getQuantity());
            log.info("确认扣减库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }

        log.info("支付成功处理完成: orderNo={}, paymentNo={}, amount={}", orderNo, paymentNo, amount);
    }

    @Override
    @GlobalTransactional(name = "handle-payment-failed", rollbackFor = Exception.class)
    public void handlePaymentFailed(String orderNo, String paymentNo, String failReason, LocalDateTime failTime) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(5); // 已取消
        order.setCancelTime(failTime);
        orderMapper.updateById(order);

        // 4. 释放库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            log.info("释放库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }

        log.info("支付失败处理完成: orderNo={}, paymentNo={}, reason={}", orderNo, paymentNo, failReason);
    }

    @Override
    @GlobalTransactional(name = "handle-payment-timeout", rollbackFor = Exception.class)
    public void handlePaymentTimeout(String orderNo, String paymentNo, Integer timeoutMinutes, LocalDateTime timeoutTime) {
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new RuntimeException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(5); // 已取消
        order.setCancelTime(timeoutTime);
        orderMapper.updateById(order);

        // 4. 释放库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            log.info("释放库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
        }

        log.info("支付超时处理完成: orderNo={}, paymentNo={}, timeoutMinutes={}", orderNo, paymentNo, timeoutMinutes);
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
