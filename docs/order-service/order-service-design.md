# 订单服务设计文档

## 1. 服务概述

订单服务是电商系统的核心服务之一，负责订单的全生命周期管理，包括订单的创建、查询、更新、取消，以及订单状态管理、支付管理等功能。该服务采用微服务架构设计，与其他服务通过RESTful API进行通信。

## 2. 技术栈

| 类别 | 技术 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础框架 | Spring Boot | 4.0.3 | 应用基础框架 |
| 微服务框架 | Spring Cloud Alibaba | 2023.0.1.0 | 微服务生态 |
| 注册中心 | Nacos | 2.2.0 | 服务注册与发现 |
| 配置中心 | Nacos Config | 2.2.0 | 分布式配置管理 |
| 分布式事务 | Seata | 1.6.0 | 分布式事务管理 |
| 分布式ID | Snowflake | - | 分布式ID生成 |
| 限流熔断 | Sentinel | 1.8.6 | 服务限流与熔断 |
| 数据库 | MySQL | 8.0 | 持久化存储 |
| 缓存 | Redis | 7.0+ | 缓存与预扣库存 |
| ORM框架 | MyBatis-Plus | 3.5.0 | 数据库操作 |
| 日志 | Logback + SkyWalking | - | 日志收集与链路追踪 |

## 3. 目录结构

```plaintext
order-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pdd/
│   │   │           └── order/
│   │   │               ├── OrderServiceApplication.java     # 应用启动类
│   │   │               ├── config/                           # 配置类
│   │   │               │   ├── RedisConfig.java              # Redis配置
│   │   │               │   ├── SeataConfig.java              # Seata配置
│   │   │               │   └── SentinelConfig.java           # Sentinel配置
│   │   │               ├── controller/                       # 控制器
│   │   │               │   ├── OrderController.java          # 订单管理
│   │   │               │   └── OrderItemController.java       # 订单项管理
│   │   │               ├── dto/                              # 数据传输对象
│   │   │               │   ├── OrderDTO.java                 # 订单DTO
│   │   │               │   ├── OrderCreateDTO.java           # 订单创建DTO
│   │   │               │   └── OrderItemDTO.java             # 订单项DTO
│   │   │               ├── entity/                           # 实体类
│   │   │               │   ├── Order.java                    # 订单实体
│   │   │               │   └── OrderItem.java                # 订单项实体
│   │   │               ├── mapper/                           # MyBatis映射
│   │   │               │   ├── OrderMapper.java               # 订单映射
│   │   │               │   └── OrderItemMapper.java           # 订单项映射
│   │   │               ├── service/                          # 业务逻辑
│   │   │               │   ├── OrderService.java              # 订单服务
│   │   │               │   ├── OrderItemService.java          # 订单项服务
│   │   │               │   └── InventoryService.java          # 库存服务（远程调用）
│   │   │               ├── util/                             # 工具类
│   │   │               │   ├── IdGenerator.java              # 分布式ID生成
│   │   │               │   └── OrderNoGenerator.java          # 订单号生成
│   │   │               └── vo/                               # 视图对象
│   │   │                   ├── OrderVO.java                  # 订单VO
│   │   │                   └── OrderItemVO.java               # 订单项VO
│   │   └── resources/
│   │       ├── application.yml                              # 应用配置
│   │       ├── application-dev.yml                          # 开发环境配置
│   │       └── mapper/                                       # MyBatis映射文件
│   │           ├── OrderMapper.xml                          # 订单映射文件
│   │           └── OrderItemMapper.xml                      # 订单项映射文件
│   └── test/                                                 # 测试代码
├── pom.xml                                                   # Maven依赖
└── Dockerfile                                                # Docker构建文件
```

## 4. 核心功能

### 4.1 订单管理

- **订单创建**：创建新订单，生成订单号
- **订单查询**：查询订单列表、订单详情
- **订单更新**：更新订单状态、订单信息
- **订单取消**：取消未支付的订单
- **订单退款**：处理订单退款请求

### 4.2 分布式事务

- **订单创建**：确保订单创建、库存扣减、支付记录的一致性
- **订单取消**：确保订单取消、库存回滚的一致性
- **订单退款**：确保退款操作的一致性

### 4.3 库存管理

- **预扣库存**：下单时使用Redis预扣库存
- **库存确认**：支付成功后确认扣减库存
- **库存释放**：订单取消时释放预扣库存

### 4.4 限流熔断

- **接口限流**：使用Sentinel对接口进行限流
- **服务熔断**：当服务异常时进行熔断保护

## 5. 数据库设计

### 5.1 订单表 (`order`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 订单ID |
| `order_no` | `VARCHAR(32)` | `UNIQUE NOT NULL` | 订单号 |
| `user_id` | `BIGINT` | `NOT NULL` | 用户ID |
| `total_amount` | `DECIMAL(10,2)` | `NOT NULL` | 总金额 |
| `actual_amount` | `DECIMAL(10,2)` | `NOT NULL` | 实际支付金额 |
| `payment_status` | `TINYINT` | `DEFAULT 0` | 支付状态（0:未支付,1:已支付,2:部分退款,3:全额退款） |
| `order_status` | `TINYINT` | `DEFAULT 0` | 订单状态（0:待付款,1:待发货,2:待收货,3:待评价,4:已完成,5:已取消） |
| `shipping_address` | `VARCHAR(255)` | `NOT NULL` | 收货地址 |
| `receiver_name` | `VARCHAR(50)` | `NOT NULL` | 收货人姓名 |
| `receiver_phone` | `VARCHAR(20)` | `NOT NULL` | 收货人电话 |
| `payment_time` | `TIMESTAMP` | | 支付时间 |
| `shipping_time` | `TIMESTAMP` | | 发货时间 |
| `confirm_time` | `TIMESTAMP` | | 确认收货时间 |
| `cancel_time` | `TIMESTAMP` | | 取消时间 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 5.2 订单项表 (`order_item`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 订单项ID |
| `order_id` | `BIGINT` | `NOT NULL` | 订单ID |
| `product_id` | `BIGINT` | `NOT NULL` | 商品ID |
| `product_sku_id` | `BIGINT` | | 商品SKU ID |
| `product_name` | `VARCHAR(200)` | `NOT NULL` | 商品名称 |
| `product_sku` | `VARCHAR(255)` | | 商品SKU信息 |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | 商品价格 |
| `quantity` | `INT` | `NOT NULL` | 商品数量 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

## 6. API设计

### 6.1 订单相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/orders` | `GET` | `OrderController` | 获取订单列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/orders/{id}` | `GET` | `OrderController` | 获取订单详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/orders` | `POST` | `OrderController` | 创建订单 | `{"userId": 1, "items": [{"productId": 1, "quantity": 2}], "addressId": 1}` | `{"code": 200, "message": "success", "data": {"orderId": 1, "orderNo": "20260224000001"}}` |
| `/api/orders/{id}` | `PUT` | `OrderController` | 更新订单 | `{"orderStatus": 1}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/orders/{id}` | `DELETE` | `OrderController` | 删除订单 | N/A | `{"code": 200, "message": "success"}` |
| `/api/orders/{id}/cancel` | `POST` | `OrderController` | 取消订单 | N/A | `{"code": 200, "message": "success"}` |
| `/api/orders/{id}/pay` | `POST` | `OrderController` | 支付订单 | `{"paymentMethod": "wechat"}` | `{"code": 200, "message": "success", "data": {"paymentUrl": "..."}}` |
| `/api/orders/{id}/confirm` | `POST` | `OrderController` | 确认收货 | N/A | `{"code": 200, "message": "success"}` |

### 6.2 订单项相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/orders/{orderId}/items` | `GET` | `OrderItemController` | 获取订单项列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/orders/{orderId}/items/{id}` | `GET` | `OrderItemController` | 获取订单项详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |

## 7. 核心配置

### 7.1 应用配置 (`application.yml`)

```yaml
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:mysql://localhost:3306/pdd_order?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 2
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
    seata:
      tx-service-group: order-service-group

server:
  port: 8083
  servlet:
    context-path: /order

# Sentinel配置
sentinel:
  transport:
    dashboard: localhost:8080
  eager: true

# 日志配置
logging:
  level:
    com.pdd.order: info
  config:
    classpath: logback-spring.xml
```

### 7.2 Seata配置

```java
@Configuration
public class SeataConfig {

    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        return new GlobalTransactionScanner("order-service", "order-service-group");
    }
}
```

## 8. 核心代码

### 8.1 订单服务

```java
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 创建订单
     */
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public OrderCreateResult createOrder(OrderCreateDTO createDTO) {
        // 1. 生成订单ID和订单号
        Long orderId = idGenerator.nextId();
        String orderNo = OrderNoGenerator.generate();

        // 2. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 3. 预扣库存
        for (OrderItemCreateDTO itemDTO : createDTO.getItems()) {
            // 预扣库存
            boolean success = inventoryService.preDeductStock(itemDTO.getProductId(), itemDTO.getQuantity());
            if (!success) {
                throw new BusinessException("商品库存不足");
            }

            // 计算金额
            // 这里应该调用商品服务获取商品价格，简化处理
            BigDecimal price = BigDecimal.valueOf(100); // 模拟价格
            BigDecimal itemAmount = price.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setId(idGenerator.nextId());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(price);
            orderItem.setProductName("商品名称"); // 模拟商品名称
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

        // 6. 构建返回结果
        OrderCreateResult result = new OrderCreateResult();
        result.setOrderId(orderId);
        result.setOrderNo(orderNo);
        return result;
    }

    /**
     * 取消订单
     */
    @GlobalTransactional(name = "cancel-order", rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new BusinessException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(5); // 已取消
        order.setCancelTime(new Date());
        orderMapper.updateById(order);

        // 4. 释放库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
        }
    }

    /**
     * 支付订单
     */
    @GlobalTransactional(name = "pay-order", rollbackFor = Exception.class)
    public void payOrder(Long orderId, String paymentMethod) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getOrderStatus() != 0) { // 不是待付款状态
            throw new BusinessException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setOrderStatus(1); // 待发货
        order.setPaymentStatus(1); // 已支付
        order.setPaymentTime(new Date());
        orderMapper.updateById(order);

        // 4. 确认扣减库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            inventoryService.confirmDeductStock(item.getProductId(), item.getQuantity());
        }
    }
}
```

### 8.2 库存服务（远程调用）

```java
@Service
public class InventoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 预扣库存
     */
    public boolean preDeductStock(Long productId, Integer quantity) {
        // 1. 生成库存键
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 获取当前库存
        Integer currentStock = (Integer) redisTemplate.opsForValue().get(stockKey);
        if (currentStock == null) {
            // 从商品服务获取库存
            currentStock = getStockFromProductService(productId);
            redisTemplate.opsForValue().set(stockKey, currentStock);
        }

        // 3. 获取预扣库存
        Integer preDeductStock = (Integer) redisTemplate.opsForValue().get(preDeductKey);
        if (preDeductStock == null) {
            preDeductStock = 0;
        }

        // 4. 检查库存是否充足
        if (currentStock - preDeductStock < quantity) {
            return false;
        }

        // 5. 预扣库存
        redisTemplate.opsForValue().increment(preDeductKey, quantity);
        return true;
    }

    /**
     * 确认扣减库存
     */
    public void confirmDeductStock(Long productId, Integer quantity) {
        // 1. 生成库存键
        String stockKey = "product:stock:" + productId;
        String preDeductKey = "product:pre_deduct:" + productId;

        // 2. 扣减库存
        redisTemplate.opsForValue().decrement(stockKey, quantity);
        // 3. 减少预扣库存
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);

        // 4. 调用商品服务扣减数据库库存
        deductStockFromProductService(productId, quantity);
    }

    /**
     * 释放库存
     */
    public void releaseStock(Long productId, Integer quantity) {
        // 生成预扣库存键
        String preDeductKey = "product:pre_deduct:" + productId;
        // 减少预扣库存
        redisTemplate.opsForValue().decrement(preDeductKey, quantity);
    }

    /**
     * 从商品服务获取库存
     */
    private Integer getStockFromProductService(Long productId) {
        // 调用商品服务获取库存，简化处理
        return 100; // 模拟库存
    }

    /**
     * 调用商品服务扣减库存
     */
    private void deductStockFromProductService(Long productId, Integer quantity) {
        // 调用商品服务扣减库存，简化处理
    }
}
```

### 8.3 分布式ID生成

```java
@Component
public class IdGenerator {

    private final Snowflake snowflake;

    public IdGenerator() {
        // 工作机器ID，实际应该从配置或环境变量获取
        long workerId = 1;
        // 数据中心ID，实际应该从配置或环境变量获取
        long datacenterId = 1;
        this.snowflake = new Snowflake(workerId, datacenterId);
    }

    public Long nextId() {
        return snowflake.nextId();
    }
}
```

### 8.4 订单号生成

```java
public class OrderNoGenerator {

    /**
     * 生成订单号
     */
    public static String generate() {
        // 订单号格式：年月日时分秒 + 6位随机数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());
        String randomPart = String.format("%06d", new Random().nextInt(1000000));
        return datePart + randomPart;
    }
}
```

## 9. 集成方案

### 9.1 服务注册与发现

- 使用Nacos作为服务注册中心，订单服务启动时自动注册到Nacos
- 其他服务通过服务名调用订单服务

### 9.2 网关集成

- API网关通过Nacos发现订单服务
- 网关统一处理认证、限流、日志等横切关注点
- 路由规则配置：将`/order/**`路径路由到订单服务

### 9.3 商品服务集成

- 订单服务调用商品服务的库存扣减接口
- 使用Seata分布式事务确保订单创建和库存扣减的一致性

### 9.4 支付服务集成

- 订单服务调用支付服务的支付接口
- 支付服务回调订单服务更新支付状态

## 10. 部署方案

### 10.1 容器化部署

- 使用Docker容器化订单服务
- 配置Docker Compose实现多服务编排

### 10.2 集群部署

- 部署多个订单服务实例
- 通过Nacos实现服务发现和负载均衡
- Seata Server部署为集群模式，提高可靠性

## 11. 注意事项

1. **分布式事务**：使用Seata确保跨服务操作的事务一致性
2. **库存管理**：使用Redis预扣库存，提高并发性能
3. **订单号生成**：确保订单号的唯一性和可读性
4. **接口限流**：对订单创建等敏感接口进行限流保护
5. **异常处理**：妥善处理各种异常情况，确保分布式事务正确回滚
6. **幂等性**：确保订单创建等操作的幂等性
7. **监控告警**：对订单异常、库存异常等情况进行监控
8. **性能优化**：考虑高并发场景下的性能优化

## 12. 扩展计划

1. **订单拆分**：支持大订单拆分为多个子订单
2. **订单超时**：实现订单超时自动取消功能
3. **订单评价**：集成订单评价功能
4. **订单统计**：实现订单数据统计和分析
5. **订单轨迹**：实现订单状态变更轨迹
6. **批量操作**：支持批量创建、批量取消等操作
7. **订单导出**：支持订单数据导出
8. **多语言支持**：支持多语言订单管理

---

**文档版本**：v1.0
**编写日期**：2026-02-24
**编写人**：System Designer